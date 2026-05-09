package com.ecommerce.cartrecovery.service;

import com.ecommerce.cartrecovery.dto.CartActivityEvent;
import com.ecommerce.cartrecovery.entity.CartActivityLogEntity;
import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationConfigEntity;
import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationScheduleEntity;
import com.ecommerce.cartrecovery.entity.Cart;
import com.ecommerce.cartrecovery.enums.CartActivityType;
import com.ecommerce.cartrecovery.enums.ScheduleStatus;
import com.ecommerce.cartrecovery.notification.kafka.producer.NotificationEventProducer;
import com.ecommerce.cartrecovery.redis.RedisRepository;
import com.ecommerce.cartrecovery.repository.CartActivityLogRepository;
import com.ecommerce.cartrecovery.repository.CartRecoveryNotificationConfigRepository;
import com.ecommerce.cartrecovery.repository.CartRecoveryNotificationScheduleRepository;
import com.ecommerce.cartrecovery.request.CartRecoveryNotificationConfigRequest;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartRecoveryService {

    private static final Logger logger = LoggerFactory.getLogger(CartRecoveryService.class);

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private CartRecoveryNotificationConfigRepository cartRecoveryNotificationConfigRepository;

    @Autowired
    private CartRecoveryNotificationScheduleRepository cartRecoveryNotificationScheduleRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private NotificationEventProducer notificationEventProducer;

    @Autowired
    private CartActivityLogRepository cartActivityLogRepository;

    private static final String CART_UPDATE_EVENTS_SET = "cart_update_events_set";
    private static final Integer Abandoned_cart_inactive_duration_in_minute = 1;

    public void processCartUpdateEvent(CartActivityEvent cartActivityEvent) {
        CartActivityLogEntity entity =
                CartActivityLogEntity.builder()
                        .cartId(cartActivityEvent.getCartId())
                        .activityType(cartActivityEvent.getActivityType())
                        .activityTimestamp(cartActivityEvent.getActivityTimestamp())
                        .build();

        cartActivityLogRepository.save(entity);

        if (cartActivityEvent.getActivityType().equals(CartActivityType.CHECKOUT)) {
            logger.info("[CartRecoveryService] cart status is checked out {} ", cartActivityEvent);
            return;
        }

        long timestamp = cartActivityEvent.getActivityTimestamp()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long scheduledTimestamp =
                timestamp + (Abandoned_cart_inactive_duration_in_minute * 60 * 1000L);
        redisRepository.zAdd(CART_UPDATE_EVENTS_SET, cartActivityEvent.getCartId().toString(), scheduledTimestamp);
        logger.info("[CartRecoveryService] successfully saved in redis");
    }

    public String getCartUpdatedTimestampRedisKey(BigInteger cartId) {
        return "cart::updatedTimestamp::v1::" + cartId;
    }

    public void processAbandonedCart() {
        long currentTime = System.currentTimeMillis();
        Map<String, LocalDateTime> cartToUpdatedTimestampMap = new HashMap<>();

        Set<ZSetOperations.TypedTuple<String>> tuples =
               redisRepository.getCartIdsWithScores(CART_UPDATE_EVENTS_SET, currentTime);

        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String cartId = tuple.getValue();
            Double score = tuple.getScore();
            LocalDateTime scheduledTime =
                    Instant.ofEpochMilli(score.longValue())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
            cartToUpdatedTimestampMap.put(cartId, scheduledTime);
        }


        if (!cartToUpdatedTimestampMap.isEmpty()) {
            redisRepository.delete(CART_UPDATE_EVENTS_SET, cartToUpdatedTimestampMap.keySet());
        }

        //add schedule
        for(String cartId : cartToUpdatedTimestampMap.keySet()) {
            createCartRecoveryNotificationSchedule(new BigInteger(cartId), cartToUpdatedTimestampMap.get(cartId), 1);
        }

        logger.info("[abandoned cart ids] {}", cartToUpdatedTimestampMap.keySet());

    }

    public void createCartRecoveryNotificationSchedule(BigInteger cartId, LocalDateTime lastActivityTimestamp, Integer stage) {
        CartRecoveryNotificationConfigEntity config = getConfigByStage(stage);
        if (config == null) {
            logger.error("[CartRecoveryService] Stage {} is not configured for notification", stage);
            return;
        }

        Cart cart = cartService.getCartById(cartId);
        if (cart == null) {
            logger.error("[CartRecoveryService] no cart found with cartId {} ", cartId);
            return;
        }
        if (!cart.isPending()) {
            logger.error("[CartRecoveryService] cart already checked_out or expired {} ", cartId);
            return;
        }

        LocalDateTime nextTime = LocalDateTime.now().plusMinutes(config.getGapInMinutes());

        CartRecoveryNotificationScheduleEntity schedule = CartRecoveryNotificationScheduleEntity.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .deviceId(cart.getDeviceId())
                .nextSchedule(nextTime)
                .lastActivityTimestamp(lastActivityTimestamp)
                .status(ScheduleStatus.PENDING)
                .currentStage(config.getStage())
                .build();
        cartRecoveryNotificationScheduleRepository.save(schedule);
    }

    public void processPendingNotificationSchedule() {

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(50);
        LocalDateTime endTime = LocalDateTime.now();

        List<CartRecoveryNotificationScheduleEntity> schedules =
        cartRecoveryNotificationScheduleRepository.findByStatusAndNextScheduleBetween(
                ScheduleStatus.PENDING,
                startTime,
                endTime
        );

        logger.info("[processPendingNotificationSchedule] total record for notification {}", schedules.size());
        for (CartRecoveryNotificationScheduleEntity schedule : schedules) {
            logger.info("Processing notification for entity : {}", schedule);
            // verify cart status
            Cart cart = cartService.getCartById(schedule.getCartId());
            if (!cart.isPending()) {
                logger.info("cart already updated: {}", schedule.getId());
                schedule.setStatus(ScheduleStatus.CANCELLED);
                cartRecoveryNotificationScheduleRepository.save(schedule);
                continue;
            }

            CartActivityLogEntity entity = cartActivityLogRepository.findTopByCartIdOrderByActivityTimestampDesc(cart.getCartId()).orElse(null);
            if (entity.getActivityTimestamp().isAfter(schedule.getLastActivityTimestamp())) {
                schedule.setStatus(ScheduleStatus.CANCELLED);
                cartRecoveryNotificationScheduleRepository.save(schedule);
                logger.info("some activity has happened on cart skipping: {}", schedule.getCartId());
                continue;
            }

            notificationEventProducer.publishNotificationEvent(schedule);

            // update schedule status
            schedule.setStatus(ScheduleStatus.COMPLETED);
            cartRecoveryNotificationScheduleRepository.save(schedule);

            // update schedule
            createCartRecoveryNotificationSchedule(schedule.getCartId(), schedule.getLastActivityTimestamp(), schedule.getCurrentStage() + 1);

        }
    }

    // cart recovery notification config
    public CartRecoveryNotificationConfigEntity createConfig(CartRecoveryNotificationConfigRequest request) {
        CartRecoveryNotificationConfigEntity entity =
                CartRecoveryNotificationConfigEntity.builder()
                        .stage(request.getStage())
                        .type(request.getType())
                        .gapInMinutes(request.getGapInMinutes())
                        .template(request.getTemplate())
                        .active(request.getActive() != null ? request.getActive() : true)
                        .build();

        return cartRecoveryNotificationConfigRepository.save(entity);
    }

    public List<CartRecoveryNotificationConfigEntity> getAllConfigs() {
        return cartRecoveryNotificationConfigRepository.findAll();
    }

    public CartRecoveryNotificationConfigEntity getConfigById(BigInteger id) {
        return cartRecoveryNotificationConfigRepository.findById(id).orElse(null);
    }

    public CartRecoveryNotificationConfigEntity updateConfig(BigInteger id, CartRecoveryNotificationConfigRequest request) {
        CartRecoveryNotificationConfigEntity existing =
                cartRecoveryNotificationConfigRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Config not found with id : " + id
                                )
                        );
        existing.setStage(request.getStage());
        existing.setType(request.getType());
        existing.setGapInMinutes(request.getGapInMinutes());
        existing.setTemplate(request.getTemplate());
        existing.setActive(request.getActive() != null ? request.getActive() : true);
        return cartRecoveryNotificationConfigRepository.save(existing);
    }

    public void deleteConfig(BigInteger id) {

        CartRecoveryNotificationConfigEntity existing =
                cartRecoveryNotificationConfigRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Config not found with id : " + id
                                )
                        );

        cartRecoveryNotificationConfigRepository.delete(existing);
    }

    public CartRecoveryNotificationConfigEntity getConfigByStage(Integer stage) {
        return cartRecoveryNotificationConfigRepository
                .findFirstByStage(stage)
                .orElse(null);
    }

    public CartRecoveryNotificationConfigEntity getConfigByStageAndType(Integer stage, String type) {
        return cartRecoveryNotificationConfigRepository
                .findFirstByStageAndType(stage, type)
                .orElse(null);
    }
}