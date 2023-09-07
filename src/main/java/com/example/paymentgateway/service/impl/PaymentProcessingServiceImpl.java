package com.example.paymentgateway.service.impl;

import com.example.paymentgateway.dto.PaymentDetailsResponse;
import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.dto.PaymentResponse;
import com.example.paymentgateway.persistence.Payment;
import com.example.paymentgateway.persistence.PaymentRepository;
import com.example.paymentgateway.service.PaymentProcessingService;
import com.example.paymentgateway.simulator.BankSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingServiceImpl implements PaymentProcessingService {

    private final BankSimulator bankSimulator;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // Check if a payment with the given idempotency key already exists
        log.info("Payment request with idempotency key {}", paymentRequest.getIdempotencyKey());
        Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(paymentRequest.getIdempotencyKey());
        if (existingPayment.isPresent()) {
            // Return the details of the existing payment
            return new PaymentResponse(existingPayment.get().getId().toString(), existingPayment.get().getStatus(), "");
        }

        // If not, process the payment
        boolean isSuccessful = bankSimulator.processTransaction(paymentRequest);

        Payment payment = new Payment();
        payment.setCardNumber(paymentRequest.getCardNumber());
        payment.setExpiryMonth(paymentRequest.getExpiryMonth());
        payment.setExpiryYear(paymentRequest.getExpiryYear());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setStatus(isSuccessful ? "SUCCESS" : "FAILURE");
        payment.setIdempotencyKey(paymentRequest.getIdempotencyKey());
        paymentRepository.save(payment);

        if (isSuccessful) {
            return new PaymentResponse(payment.getId().toString(), "SUCCESS", "Payment processed successfully.");
        } else {
            return new PaymentResponse(payment.getId().toString(), "FAILURE", "Payment processing failed.");
        }
    }

    @Override
    public PaymentDetailsResponse retrievePaymentDetails(String paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(Long.parseLong(paymentId));

        if (optionalPayment.isEmpty()) {
            throw new EntityNotFoundException("Payment with ID " + paymentId + " not found.");
        }

        Payment payment = optionalPayment.get();

        // Mask the card number for security reasons
        String maskedCardNumber = maskCardNumber(payment.getCardNumber());

        return new PaymentDetailsResponse(
                payment.getId().toString(),
                maskedCardNumber,
                payment.getExpiryMonth(),
                payment.getExpiryYear(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus()
        );
    }

    private String maskCardNumber(String cardNumber) {
        int length = cardNumber.length();
        String lastFourDigits = cardNumber.substring(length - 4);
        return "XXXX-XXXX-XXXX-" + lastFourDigits;
    }

}
