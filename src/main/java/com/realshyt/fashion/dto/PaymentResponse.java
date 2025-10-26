package com.realshyt.fashion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private String midtransOrderId;
    private String snapToken;
    private String snapRedirectUrl;
    private BigDecimal amount;
    private String status;
    private String message;
}
