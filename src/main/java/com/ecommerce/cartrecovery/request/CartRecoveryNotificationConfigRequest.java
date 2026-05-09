package com.ecommerce.cartrecovery.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRecoveryNotificationConfigRequest {

    @NotNull(message = "Stage is mandatory")
    @Min(value = 1, message = "Stage must be at least 1")
    private Integer stage;

    @JsonProperty("type")
    @NotBlank(message = "Type cannot be empty")
    private String type;

    @NotNull(message = "Gap in minutes is mandatory")
    @Min(value = 1, message = "Gap must be at least 1 minute")
    private Integer gapInMinutes;

    private String template;

    private Boolean active;
}