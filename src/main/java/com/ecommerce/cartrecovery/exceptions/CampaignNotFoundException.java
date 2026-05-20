package com.ecommerce.cartrecovery.exceptions;

import com.ecommerce.cartrecovery.enums.ErrorCode;

public class CampaignNotFoundException extends CartRecoveryException {
    public CampaignNotFoundException(String message) {
        super(ErrorCode.CAMPAIGN_NOT_FOUND, message);
    }

    public CampaignNotFoundException() {
        super(ErrorCode.CAMPAIGN_NOT_FOUND);
    }
}

