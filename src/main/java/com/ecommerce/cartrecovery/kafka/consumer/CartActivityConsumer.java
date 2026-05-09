package com.ecommerce.cartrecovery.kafka.consumer;


import com.ecommerce.cartrecovery.dto.CartActivityEvent;
import com.ecommerce.cartrecovery.kafka.producer.CartUpdateEventProducer;
import com.ecommerce.cartrecovery.service.CartRecoveryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@EnableKafka
public class CartActivityConsumer {

    @Autowired
    private CartRecoveryService cartRecoveryService;

    private static final Logger logger = LoggerFactory.getLogger(CartActivityConsumer.class);

    @KafkaListener(
            topics = "${kafka.topic.cart-update-events}",
            groupId = "${spring.kafka.consumer.cart-update-event.group-id}"
    )

    public void consume(String message) {
        logger.info("[CartActivityConsumer] Received cart activity event : {} ", message);
        ObjectMapper objectMapper = new ObjectMapper();
        CartActivityEvent cartActivityEvent = objectMapper.readValue(message, CartActivityEvent.class);
        cartRecoveryService.processCartUpdateEvent(cartActivityEvent);
    }
}