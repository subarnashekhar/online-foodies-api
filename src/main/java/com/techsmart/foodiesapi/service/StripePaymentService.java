package com.techsmart.foodiesapi.service;


import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.techsmart.foodiesapi.io.CreatePaymentRequest;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    public PaymentIntent createPayment(CreatePaymentRequest request) throws Exception {

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())
                .setCurrency(request.getCurrency())
                .setDescription(request.getDescription())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }
}


