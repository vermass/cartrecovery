package com.ecommerce.cartrecovery.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignRequest {

    @NotBlank(message = "Campaign name cannot be empty")
    @Size(max = 100, message = "Campaign name is too long")

    private String campaignName;

    @NotNull(message = " config is mandatory")
    private Integer recoveryConfigId;

    private String template;

    private String description;


    @Min(0) @Max(100)
    private Integer trafficPercentage = 100;

    private Boolean isActive = true;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}