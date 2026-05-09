package com.ecommerce.cartrecovery.notification.service;

import com.ecommerce.cartrecovery.entity.CampaignEntity;
import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationConfigEntity;
import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationLogEntity;
import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationScheduleEntity;
import com.ecommerce.cartrecovery.enums.NotificationType;
import com.ecommerce.cartrecovery.notification.kafka.producer.NotificationEventProducer;
import com.ecommerce.cartrecovery.repository.CartRecoveryNotificationLogRepository;
import com.ecommerce.cartrecovery.service.CampaignService;
import com.ecommerce.cartrecovery.service.CartRecoveryService;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationEventProducer notificationEventProducer;

    @Autowired
    private CartRecoveryService cartRecoveryService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CartRecoveryNotificationLogRepository cartRecoveryNotificationLogRepository;

    // email notification
    public void sendEmailNotification( CartRecoveryNotificationScheduleEntity entity) {
        logger.info("[Email] notification for scheduleId: {}", entity);
        CartRecoveryNotificationConfigEntity configEntity = cartRecoveryService.getConfigByStageAndType(entity.getCurrentStage(), "email");
        if (configEntity == null) {
            logger.error("[Email] config entity not found for email and for stage : {}", entity.getCurrentStage());
            return;
        }
        List<CampaignEntity> campaignEntityList = campaignService.getAllCampaignsByStageConfigId(configEntity.getId());

        CampaignEntity campaignEntity = filterCampaign(entity.getUserId(), campaignEntityList);
        if (campaignEntity == null) {
            logger.error("[Email] No active campaign for recovery config id : {}", configEntity.getId());
            return;
        }

        String renderedTemplate = fillTemplate(campaignEntity.getTemplate(), entity.getUserId(), entity.getCartId());
        logger.info("[Email] sent : {} ", renderedTemplate);
        saveNotificationLog(entity, configEntity, renderedTemplate);
    }

    // push notification
    public void sendPushNotification( CartRecoveryNotificationScheduleEntity entity) {
        logger.info("[Push] notification for scheduleId: {}", entity);
        CartRecoveryNotificationConfigEntity configEntity = cartRecoveryService.getConfigByStageAndType(entity.getCurrentStage(), "push");

        if (configEntity == null) {
            logger.error("[Push] config entity not found for push and for stage : {}", entity.getCurrentStage());
            return;

        }
        List<CampaignEntity> campaignEntityList = campaignService.getAllCampaignsByStageConfigId(configEntity.getId());
        CampaignEntity campaignEntity = filterCampaign(entity.getUserId(), campaignEntityList);
        if (campaignEntity == null) {
            logger.error("[Push] No active campaign for recovery config id : {}", configEntity.getId());
            return;
        }
        String renderedTemplate = fillTemplate(campaignEntity.getTemplate(), entity.getUserId(), entity.getCartId());

        logger.info("[Push] sent : {} ", renderedTemplate);
        saveNotificationLog(entity, configEntity, renderedTemplate);

    }

    public void sendSMSNotification( CartRecoveryNotificationScheduleEntity entity) {
        logger.info("[SMS] notification for scheduleId: {}", entity);
        CartRecoveryNotificationConfigEntity configEntity = cartRecoveryService.getConfigByStageAndType(entity.getCurrentStage(), "sms");

        if (configEntity == null) {
            logger.error("[SMS] config entity not found for push and for stage : {}", entity.getCurrentStage());
            return;

        }

        List<CampaignEntity> campaignEntityList = campaignService.getAllCampaignsByStageConfigId(configEntity.getId());

        CampaignEntity campaignEntity = filterCampaign(entity.getUserId(), campaignEntityList);
        if (campaignEntity == null) {
            logger.error("[SMS] No active campaign for recovery config id : {}", configEntity.getId());
            return;
        }
        String renderedTemplate = fillTemplate(campaignEntity.getTemplate(), entity.getUserId(), entity.getCartId());

        logger.info("[SMS] sent : {} ", renderedTemplate);
        saveNotificationLog(entity, configEntity, renderedTemplate);
    }

    public CampaignEntity filterCampaign(BigInteger userId, List<CampaignEntity> campaignEntityList) {
        if (campaignEntityList.isEmpty()) {
            return null;
        }
        if (userId == null) {
            return campaignEntityList.get(0);
        }

        int bucket = userId
                .mod(BigInteger.valueOf(100))
                .intValue();

        int cumulativePercentage = 0;
        for (CampaignEntity campaign : campaignEntityList) {
            cumulativePercentage += campaign.getTrafficPercentage();
            if (bucket < cumulativePercentage) {
                return campaign;
            }
        }
        return null;
    }

    public String fillTemplate(String template, BigInteger userId, BigInteger cartId) {

        template = template.replace(
                "{{userName}}",
                userId.toString()
        );

        template = template.replace(
                "{{cartId}}",
                String.valueOf(cartId)
        );

        return template;
    }

    public void saveNotificationLog(CartRecoveryNotificationScheduleEntity scheduleEntity,  CartRecoveryNotificationConfigEntity configEntity, String message) {
        CartRecoveryNotificationLogEntity entity =
                CartRecoveryNotificationLogEntity.builder()
                        .cartId(scheduleEntity.getCartId())
                        .userId(scheduleEntity.getUserId())
                        .deviceId(scheduleEntity.getDeviceId())
                        .stage(scheduleEntity.getCurrentStage())
                        .type(
                                NotificationType.valueOf(
                                        configEntity.getType()
                                                .toUpperCase()
                                )
                        )
                        .message(message)
                        .build();

        cartRecoveryNotificationLogRepository.save(entity);
    }
}
