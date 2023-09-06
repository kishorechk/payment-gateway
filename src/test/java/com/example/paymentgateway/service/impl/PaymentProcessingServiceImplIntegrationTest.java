package com.example.paymentgateway.service.impl;

import com.example.paymentgateway.PaymentGatewayApplication;
import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.persistence.Payment;
import com.example.paymentgateway.persistence.PaymentRepository;
import com.example.paymentgateway.service.PaymentProcessingService;
import com.example.paymentgateway.simulator.BankSimulator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PaymentGatewayApplication.class)
public class PaymentProcessingServiceImplIntegrationTest {

    @Autowired
    private PaymentProcessingService paymentProcessingService;

    @MockBean
    private BankSimulator bankSimulator;

    @Autowired
    private PaymentRepository paymentRepository;

    @AfterEach
    public void cleanup(){
        paymentRepository.deleteAll();
    }

    @Test
    public void testProcessPaymentSuccess() {
        PaymentRequest request = new PaymentRequest("4111111111111112", "12", "2025", "123", 100.50, "USD");
        when(bankSimulator.processTransaction(any(PaymentRequest.class))).thenReturn(true);
        var response = paymentProcessingService.processPayment(request);

        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    public void testProcessPaymentFailure() {
        PaymentRequest request = new PaymentRequest("4111111111111111", "12", "2025", "123", 100.50, "USD");
        when(bankSimulator.processTransaction(any(PaymentRequest.class))).thenReturn(false);
        var response = paymentProcessingService.processPayment(request);

        assertEquals("FAILURE", response.getStatus());
    }

    @Test
    public void testRetrievePaymentDetails_Success() {
        var payment = Payment.builder().status("SUCCESS").amount(100.0).cardNumber("4111111111111112").currency("USD").expiryMonth("12").expiryYear("23").build();
        payment = paymentRepository.save(payment);
        var result = paymentProcessingService.retrievePaymentDetails(payment.getId().toString());
        assertNotNull(result);
        assertEquals(payment.getCurrency(), result.getCurrency());
        assertEquals(payment.getExpiryMonth(), result.getExpiryMonth());
        assertEquals(payment.getExpiryYear(), result.getExpiryYear());
    }

    @Test
    public void testRetrievePaymentDetails_NotFound() {
        String paymentId = "1";
        assertThrows(EntityNotFoundException.class, () -> paymentProcessingService.retrievePaymentDetails(paymentId));

    }
}
