package com.realshyt.fashion.service;

import com.realshyt.fashion.dto.MidtransNotification;
import com.realshyt.fashion.dto.PaymentRequest;
import com.realshyt.fashion.dto.PaymentResponse;
import com.realshyt.fashion.entity.Order;
import com.realshyt.fashion.entity.Payment;
import com.realshyt.fashion.repository.OrderRepository;
import com.realshyt.fashion.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CurrencyConversionService currencyConversionService;
    private final RestTemplate restTemplate;
    
    @Value("${midtrans.server-key:SB-Mid-server-YOUR_SERVER_KEY}")
    private String midtransServerKey;
    
    @Value("${midtrans.client-key:SB-Mid-client-YOUR_CLIENT_KEY}")
    private String midtransClientKey;
    
    @Value("${midtrans.api-url:https://app.sandbox.midtrans.com/snap/v1/transactions}")
    private String midtransApiUrl;
    
    @Value("${midtrans.is-production:false}")
    private boolean isProduction;
    
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        // Validate order
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new RuntimeException("Payment already exists for this order");
        }
        
        // Get user currency (default to IDR)
        String userCurrency = "IDR"; // In production, get from request or user context
        if (order.getCurrency() != null) {
            userCurrency = order.getCurrency();
        }
        
        // Convert order amount to IDR for Midtrans (Midtrans requires IDR)
        BigDecimal orderAmount = order.getTotalPrice();
        if (!userCurrency.equals("IDR")) {
            orderAmount = currencyConversionService.toIDR(orderAmount, userCurrency);
            log.info("Converted {} {} to {} IDR", order.getTotalPrice(), userCurrency, orderAmount);
        }
        
        // Generate unique order ID for Midtrans
        String midtransOrderId = "ORDER-" + order.getId() + "-" + System.currentTimeMillis();
        
        // Create payment entity
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(orderAmount); // Amount in IDR for Midtrans
        payment.setCurrency("IDR");
        payment.setMidtransOrderId(midtransOrderId);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        
        // Build Midtrans request
        Map<String, Object> midtransRequest = buildMidtransRequest(
            order, 
            midtransOrderId, 
            request.getCustomerName(),
            request.getCustomerEmail(),
            request.getCustomerPhone(),
            orderAmount
        );
        
        try {
            // Call Midtrans API
            Map<String, Object> midtransResponse = callMidtransApi(midtransRequest);
            
            // Update payment with Midtrans response
            payment.setSnapToken((String) midtransResponse.get("token"));
            payment.setSnapRedirectUrl((String) midtransResponse.get("redirect_url"));
            payment.setTransactionId(midtransOrderId);
            
            // Save payment
            payment = paymentRepository.save(payment);
            
            // Update order status
            order.setStatus(Order.OrderStatus.PENDING_PAYMENT);
            orderRepository.save(order);
            
            log.info("Payment created successfully for order: {}", order.getId());
            
            // Return amount in user's currency for display
            BigDecimal displayAmount = userCurrency.equals("IDR") ? orderAmount 
                : currencyConversionService.fromIDR(orderAmount, userCurrency);
            
            return new PaymentResponse(
                payment.getId(),
                order.getId(),
                midtransOrderId,
                payment.getSnapToken(),
                payment.getSnapRedirectUrl(),
                displayAmount,
                payment.getStatus().toString(),
                "Payment created successfully"
            );
            
        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }
    
    private Map<String, Object> buildMidtransRequest(Order order, String orderId, 
                                                     String customerName, String customerEmail, 
                                                     String customerPhone, BigDecimal amount) {
        Map<String, Object> request = new HashMap<>();
        
        // Transaction details
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", amount.longValue());
        request.put("transaction_details", transactionDetails);
        
        // Customer details
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("first_name", customerName);
        customerDetails.put("email", customerEmail);
        customerDetails.put("phone", customerPhone);
        request.put("customer_details", customerDetails);
        
        // Item details - convert product prices to IDR if needed
        List<Map<String, Object>> itemDetails = new ArrayList<>();
        String orderCurrency = order.getCurrency() != null ? order.getCurrency() : "IDR";
        
        order.getOrderItems().forEach(item -> {
            Map<String, Object> itemDetail = new HashMap<>();
            itemDetail.put("id", item.getProduct().getId());
            
            // Convert product price to IDR for Midtrans
            BigDecimal productPrice = item.getPrice();
            if (!orderCurrency.equals("IDR")) {
                productPrice = currencyConversionService.toIDR(productPrice, orderCurrency);
            }
            
            itemDetail.put("price", productPrice.longValue());
            itemDetail.put("quantity", item.getQuantity());
            itemDetail.put("name", item.getProduct().getName());
            itemDetails.add(itemDetail);
        });
        request.put("item_details", itemDetails);
        
        // Enable payments
        Map<String, Object> enabledPayments = new HashMap<>();
        enabledPayments.put("enabled_payments", Arrays.asList(
            "credit_card", "gopay", "shopeepay", "bca_va", "bni_va", "bri_va", "other_va"
        ));
        request.put("enabled_payments", enabledPayments.get("enabled_payments"));
        
        return request;
    }
    
    private Map<String, Object> callMidtransApi(Map<String, Object> request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Basic auth with server key
            String auth = midtransServerKey + ":";
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            log.info("Calling Midtrans API: {}", midtransApiUrl);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = 
                (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.exchange(
                    midtransApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
                );
            
            log.info("Midtrans API response status: {}", response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("Midtrans API call failed with status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error calling Midtrans API: {}", e.getMessage());
            throw new RuntimeException("Failed to call Midtrans API: " + e.getMessage());
        }
    }
    
    @Transactional
    public void handleMidtransNotification(MidtransNotification notification) {
        log.info("Handling Midtrans notification for order: {}", notification.getOrder_id());
        
        // Verify signature
        if (!verifySignature(notification)) {
            log.error("Invalid signature for notification: {}", notification.getOrder_id());
            throw new RuntimeException("Invalid signature");
        }
        
        // Find payment
        Payment payment = paymentRepository.findByMidtransOrderId(notification.getOrder_id())
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Update payment based on transaction status
        payment.setPaymentType(notification.getPayment_type());
        payment.setFraudStatus(notification.getFraud_status());
        
        if (notification.getTransaction_time() != null) {
            payment.setTransactionTime(parseDateTime(notification.getTransaction_time()));
        }
        
        if (notification.getSettlement_time() != null) {
            payment.setSettlementTime(parseDateTime(notification.getSettlement_time()));
        }
        
        Order order = payment.getOrder();
        
        switch (notification.getTransaction_status()) {
            case "capture":
            case "settlement":
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                order.setStatus(Order.OrderStatus.PROCESSING);
                log.info("Payment successful for order: {}", order.getId());
                break;
                
            case "pending":
                payment.setStatus(Payment.PaymentStatus.PENDING);
                order.setStatus(Order.OrderStatus.PENDING_PAYMENT);
                break;
                
            case "deny":
            case "cancel":
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(notification.getStatus_message());
                order.setStatus(Order.OrderStatus.CANCELLED);
                log.warn("Payment failed for order: {}", order.getId());
                break;
                
            case "expire":
                payment.setStatus(Payment.PaymentStatus.EXPIRED);
                order.setStatus(Order.OrderStatus.CANCELLED);
                break;
                
            default:
                log.warn("Unknown transaction status: {}", notification.getTransaction_status());
        }
        
        paymentRepository.save(payment);
        orderRepository.save(order);
    }
    
    private boolean verifySignature(MidtransNotification notification) {
        // Implement signature verification
        // SHA512(order_id + status_code + gross_amount + server_key)
        try {
            String signatureString = notification.getOrder_id() + 
                                   notification.getStatus_code() + 
                                   notification.getGross_amount() + 
                                   midtransServerKey;
            
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(signatureString.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString().equals(notification.getSignature_key());
        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage());
            return false;
        }
    }
    
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.error("Error parsing datetime: {}", dateTimeStr);
            return LocalDateTime.now();
        }
    }
    
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }
}