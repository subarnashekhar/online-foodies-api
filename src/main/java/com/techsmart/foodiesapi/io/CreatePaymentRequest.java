package com.techsmart.foodiesapi.io;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull
    private Long amount; // in cents e.g., 5000 = R50/R50.00

    @NotNull
    private String currency; // usd, inr

    @NotNull
    private String description;

}
