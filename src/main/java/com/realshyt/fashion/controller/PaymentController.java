package com.realshyt.fashion.controller;

import com.realshyt.fashion.dto.MidtransNotification;
import com.realshyt.fashion.dto.PaymentRequest;
import com.realshyt.fashion.dto.PaymentResponse;
import com.realshyt.fashion.entity.Payment;
import com.realshyt.fashion.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Create payment for an order
     * POST /api/payments/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
        try {
            log.info("Creating payment for order: {}", request.getOrderId());
            PaymentResponse response = paymentService.createPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Handle Midtrans webhook notification
     * POST /api/payments/notification
     */
    @PostMapping("/notification")
    public ResponseEntity<?> handleNotification(@RequestBody MidtransNotification notification) {
        try {
            log.info("Received Midtrans notification for order: {}", notification.getOrder_id());
            paymentService.handleMidtransNotification(notification);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notification processed");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing notification: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Get payment status by order ID
     * GET /api/payments/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("orderId", payment.getOrder().getId());
            response.put("amount", payment.getAmount());
            response.put("status", payment.getStatus());
            response.put("paymentMethod", payment.getPaymentMethod());
            response.put("transactionId", payment.getTransactionId());
            response.put("createdAt", payment.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting payment: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    /**
     * Get payment by ID
     * GET /api/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        try {
            // Implement if needed
            Map<String, String> response = new HashMap<>();
            response.put("message", "Endpoint not yet implemented");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    /**
     * Test endpoint
     * GET /api/payments/test
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Payment API is running");
        response.put("message", "Realshyt Fashion Payment Service");
        return ResponseEntity.ok(response);
    }
}