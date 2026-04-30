package com.techsmart.foodiesapi.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PaymentIntentResponse {

    private String id;
    private String clientSecret;
    private Long amount;
    private String currency;
    private String status;
}