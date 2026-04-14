package com.techsmart.foodiesapi.service;

import com.techsmart.foodiesapi.io.CartRequest;
import com.techsmart.foodiesapi.io.CartResponse;

public interface CartService {

    CartResponse addToCart(CartRequest request);

    CartResponse getCart();

    void clearCart();

    CartResponse removeFromCart(CartRequest cartRequest);
}
