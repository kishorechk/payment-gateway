package com.example.paymentgateway.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsResponse {
    private String paymentId;
    private String maskedCardNumber;
    private String expiryMonth;
    private String expiryYear;
    private Double amount;
    private String currency;
    private String status;
}