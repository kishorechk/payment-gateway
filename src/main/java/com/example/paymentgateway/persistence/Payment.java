package com.example.paymentgateway.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private Double amount;
    private String currency;
    private String status;
    @Column(unique = true)  // This ensures that the idempotency key is unique across all payments
    private String idempotencyKey;
}
