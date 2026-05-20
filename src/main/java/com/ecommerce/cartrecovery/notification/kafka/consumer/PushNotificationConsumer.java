package com.ecommerce.cartrecovery.notification.kafka.consumer;

import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationScheduleEntity;
import com.ecommerce.cartrecovery.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@EnableKafka
public class PushNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationConsumer.class);

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topic.cart-recovery-notification-topic}",
            groupId = "${spring.kafka.consumer.cart-recovery-notification.push.group-id}"
    )

    public void consume(String message) {
        logger.info(
                "[PushNotificationConsumer] Received push notification event: {}",
                message
        );
        ObjectMapper objectMapper = new ObjectMapper();
        CartRecoveryNotificationScheduleEntity entity = objectMapper.readValue(message, CartRecoveryNotificationScheduleEntity.class);
        notificationService.sendPushNotification(entity);
    }
}
