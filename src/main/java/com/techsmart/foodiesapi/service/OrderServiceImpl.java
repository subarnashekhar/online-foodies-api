package com.techsmart.foodiesapi.service;

import com.stripe.model.PaymentIntent;
import com.techsmart.foodiesapi.entity.OrderEntity;
import com.techsmart.foodiesapi.io.CreatePaymentRequest;
import com.techsmart.foodiesapi.io.OrderRequest;
import com.techsmart.foodiesapi.io.OrderResponse;
import com.techsmart.foodiesapi.repository.CartRespository;
import com.techsmart.foodiesapi.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CartRespository cartRespository;

    @Autowired
    private StripePaymentService stripePaymentService;

    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) throws Exception {
        OrderEntity newOrder = convertToEntity(request);
        newOrder = orderRepository.save(newOrder);


        // Create a PaymentIntent for STripe
        CreatePaymentRequest createPaymentRequest = CreatePaymentRequest.builder()
                .amount((long)newOrder.getAmount() * 100) // Stripe expects amount in cents
                .currency("USD")
                .description("Order Payment for Order ID: " + newOrder.getId())
                .build();

        PaymentIntent intent = stripePaymentService.createPayment(createPaymentRequest);

        log.info("Payment Intent: " + intent.toString());


        //create razorpay payment order
//        RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", newOrder.getAmount() * 100);
//        orderRequest.put("currency", "INR");
//        orderRequest.put("payment_capture", 1);

//        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        newOrder.setStripeOrderId(intent.getId());
        newOrder.setAmount((double) intent.getAmount() /100); // Convert back to dollars
        String loggedInUserId = userService.findByUserId();
        newOrder.setUserId(loggedInUserId);
        newOrder.setStripeClientSecret(intent.getClientSecret());
        newOrder = orderRepository.save(newOrder);
        return convertToResponse(newOrder);
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        OrderEntity existingOrder = orderRepository.findByStripeOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setPaymentStatus(status);
        existingOrder.setStripeClientSecret(paymentData.get("razorpay_signature"));
//        existingOrder.setRazorpayPaymentId(paymentData.get("razorpay_payment_id"));
        orderRepository.save(existingOrder);
        if ("paid".equalsIgnoreCase(status)) {
            cartRespository.deleteByUserId(existingOrder.getUserId());
        }
        System.out.println("Payment Verified");

    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.findByUserId();
        List<OrderEntity> list = orderRepository.findByUserId(loggedInUserId);
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderResponse> getOrdersOfAllUsers() {
        List<OrderEntity> list = orderRepository.findAll();
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        entity.setOrderStatus(status);
        orderRepository.save(entity);
    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .amount(newOrder.getAmount())
                .userAddress(newOrder.getUserAddress())
                .userId(newOrder.getUserId())
                .stripeOrderId(newOrder.getStripeOrderId())
                .paymentStatus(newOrder.getPaymentStatus())
                .orderStatus(newOrder.getOrderStatus())
                .email(newOrder.getEmail())
                .phoneNumber(newOrder.getPhoneNumber())
                .orderedItems(newOrder.getOrderedItems())
                .stripeClientSecret(newOrder.getStripeClientSecret())
                .build();
    }

    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .orderedItems(request.getOrderedItems())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(request.getOrderStatus())
                .build();
    }
}
