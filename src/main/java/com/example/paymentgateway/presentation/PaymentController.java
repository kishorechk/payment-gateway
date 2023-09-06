package com.example.paymentgateway.presentation;

import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.service.PaymentProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment", description = "Payment processing and retrieval")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentProcessingService paymentProcessingService;

    @PostMapping
    @Operation(summary = "Process a payment", description = "Processes a payment through the payment gateway.")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        // Logic to process the payment
        var response = paymentProcessingService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Retrieve payment details", description = "Retrieves details of a previously made payment using its identifier.")
    public ResponseEntity<?> retrievePaymentDetails(@PathVariable String paymentId) {
        // Logic to retrieve payment details
        var response = paymentProcessingService.retrievePaymentDetails(paymentId);
        return ResponseEntity.ok(response);
    }
}
