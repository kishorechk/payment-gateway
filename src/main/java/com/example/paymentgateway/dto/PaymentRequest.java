package com.example.paymentgateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "Card number is required.")
    @Size(min = 16, max = 16, message = "Card number should be 16 digits.")
    private String cardNumber;

    @NotBlank(message = "Expiry month is required.")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiry month should be MM format.")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required.")
    @Pattern(regexp = "^20\\d{2}$", message = "Expiry year should be YYYY format.")
    private String expiryYear;

    @NotBlank(message = "CVV is required.")
    @Size(min = 3, max = 4, message = "CVV should be 3 or 4 digits.")
    private String cvv;

    @NotNull(message = "Amount is required.")
    @Positive(message = "Amount should be positive.")
    private Double amount;

    @NotBlank(message = "Currency is required.")
    private String currency;

    @NotBlank(message = "Idempotency Key is required.")
    private String idempotencyKey;
}