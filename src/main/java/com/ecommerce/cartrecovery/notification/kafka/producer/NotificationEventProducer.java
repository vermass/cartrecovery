package com.ecommerce.cartrecovery.notification.kafka.producer;

import com.ecommerce.cartrecovery.dto.CartActivityEvent;
import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationScheduleEntity;
import com.ecommerce.cartrecovery.kafka.producer.CartUpdateEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.Future;

@Service
public class NotificationEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventProducer.class);

    @Value("${kafka.topic.cart-recovery-notification-topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishNotificationEvent(CartRecoveryNotificationScheduleEntity entity) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(entity);
            logger.info("[NotificationEventProducer] Publishing cart update event to topic: {}, message: {}", topic, message);
            Future<?> res = kafkaTemplate.send(topic, message);
            logger.info("[NotificationEventProducer] result {} ", res.get());
        } catch (Exception e) {
            logger.error("[NotificationEventProducer] error occurred producing cart update event {} " , entity);
        }
    }
}
