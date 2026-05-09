package com.ecommerce.cartrecovery.controller;

import com.ecommerce.cartrecovery.entity.CampaignEntity;
import com.ecommerce.cartrecovery.request.CampaignRequest;
import com.ecommerce.cartrecovery.request.CartRecoveryNotificationConfigRequest;
import com.ecommerce.cartrecovery.service.CampaignService;
import com.ecommerce.cartrecovery.service.CartRecoveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/cartrecovery")
public class CartRecoveryController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CartRecoveryService cartRecoveryService;

    // ************** campaigns crud *******************
    @PostMapping("/campaigns")
    public ResponseEntity<?> create(@RequestBody @Validated CampaignRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                return ResponseEntity.badRequest().body("invalid start date or end date");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.createCampaign(request));
    }

    @GetMapping("/campaigns")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<?> getById(@PathVariable BigInteger id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @PutMapping("/campaigns/{id}")
    public ResponseEntity<?> update(@PathVariable BigInteger id, @RequestBody CampaignEntity campaign) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, campaign));
    }

    @DeleteMapping("campaigns/{id}")
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    // ******* notification config crud ***********
    @PostMapping("/notification/configs")
    public ResponseEntity<?> createConfig(@RequestBody @Valid CartRecoveryNotificationConfigRequest request) {
        return ResponseEntity.ok(cartRecoveryService.createConfig(request));
    }

    @GetMapping("/notification/configs")
    public ResponseEntity<?> getAllConfigs() {
        return ResponseEntity.ok(cartRecoveryService.getAllConfigs());
    }

    @GetMapping("/notification/configs/{id}")
    public ResponseEntity<?> getConfigById(@PathVariable BigInteger id) {
        return ResponseEntity.ok(cartRecoveryService.getConfigById(id));
    }

    @PutMapping("/notification/configs/{id}")
    public ResponseEntity<?> updateConfig(
            @PathVariable BigInteger id,
            @RequestBody CartRecoveryNotificationConfigRequest request
    ) {

        return ResponseEntity.ok(cartRecoveryService.updateConfig(id, request));
    }

    @DeleteMapping("/notification/configs/{id}")
    public String deleteConfig(@PathVariable BigInteger id) {
        cartRecoveryService.deleteConfig(id);
        return "Config deleted successfully";
    }
}