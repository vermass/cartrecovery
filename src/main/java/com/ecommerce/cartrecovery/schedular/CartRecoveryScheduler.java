package com.ecommerce.cartrecovery.schedular;

import com.ecommerce.cartrecovery.service.CartRecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CartRecoveryScheduler {

    @Autowired
    private CartRecoveryService cartRecoveryService;

    private static final Logger logger = LoggerFactory.getLogger(CartRecoveryScheduler.class);

    @Scheduled(cron = "*/30 * * * * *")
    public void processAbandonedCarts() {

        logger.info("[CartRecoveryScheduler] starting abandoned cart processing job");

        cartRecoveryService.processAbandonedCart();
    }
}
