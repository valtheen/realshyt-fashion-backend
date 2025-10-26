package com.realshyt.fashion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MidtransNotification {
    
    @JsonProperty("order_id")
    private String order_id;
    
    @JsonProperty("transaction_status")
    private String transaction_status;
    
    @JsonProperty("payment_type")
    private String payment_type;
    
    @JsonProperty("fraud_status")
    private String fraud_status;
    
    @JsonProperty("transaction_time")
    private String transaction_time;
    
    @JsonProperty("settlement_time")
    private String settlement_time;
    
    @JsonProperty("status_code")
    private String status_code;
    
    @JsonProperty("gross_amount")
    private String gross_amount;
    
    @JsonProperty("status_message")
    private String status_message;
    
    @JsonProperty("signature_key")
    private String signature_key;
}
