package com.ecommerce.cartrecovery.schedular;

import com.ecommerce.cartrecovery.service.CartRecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CartRecoveryNotificationSchedular {
    @Autowired
    private CartRecoveryService cartRecoveryService;

    @Scheduled(cron = "0 */1 * * * *")
    public void processAbandonedCarts() {

        System.out.println("[Abandoned cart notification schedular] Running abandoned cart scheduler");

        cartRecoveryService.processPendingNotificationSchedule();
    }
}
