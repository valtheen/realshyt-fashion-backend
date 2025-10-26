package com.realshyt.fashion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currency = "IDR";
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    // Midtrans specific fields
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(name = "midtrans_order_id", unique = true)
    private String midtransOrderId;
    
    @Column(name = "snap_token")
    private String snapToken;
    
    @Column(name = "snap_redirect_url")
    private String snapRedirectUrl;
    
    @Column(name = "payment_type")
    private String paymentType;
    
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
    
    @Column(name = "settlement_time")
    private LocalDateTime settlementTime;
    
    @Column(name = "fraud_status")
    private String fraudStatus;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        SUCCESS,
        FAILED,
        EXPIRED,
        CANCELLED,
        REFUNDED
    }
}