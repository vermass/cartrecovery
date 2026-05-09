package com.ecommerce.cartrecovery.kafka.producer;

import com.ecommerce.cartrecovery.dto.CartActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.Future;

@Service
public class CartUpdateEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(CartUpdateEventProducer.class);

    @Value("${kafka.topic.cart-update-events}")
    private String topic;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    public void publishCartUpdateEvent(CartActivityEvent event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(event);
            logger.info("[CartUpdateEventProducer] Publishing notification event to topic: {}, message: {}", topic, message);
            Future<?> res = kafkaTemplate.send(topic, message);
            logger.info("[CartUpdateEventProducer] result {} ", res.get());
        } catch (Exception e) {
            logger.error("[CartUpdateEventProducer] error occurred producing notification event {} " , event);
        }
    }
}
