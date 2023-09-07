package com.example.paymentgateway.simulator.impl;

import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.simulator.BankSimulator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BankSimulatorImplIntegrationTest {

    @Autowired
    private BankSimulator bankSimulator;

    @Test
    public void testProcessTransactionSuccess() {
        PaymentRequest request = new PaymentRequest("4111111111111112", "12", "2025", "123", 100.50, "USD", UUID.randomUUID().toString());
        assertTrue(bankSimulator.processTransaction(request));
    }

    @Test
    public void testProcessTransactionFailure() {
        PaymentRequest request = new PaymentRequest("4111111111111113", "12", "2025", "123", 100.50, "USD", UUID.randomUUID().toString());
        assertFalse(bankSimulator.processTransaction(request));
    }
}
