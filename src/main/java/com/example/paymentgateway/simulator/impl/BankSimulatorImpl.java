package com.example.paymentgateway.simulator.impl;

import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.simulator.BankSimulator;
import org.springframework.stereotype.Service;

@Service
public class BankSimulatorImpl implements BankSimulator {

    @Override
    public boolean processTransaction(PaymentRequest paymentRequest) {
        // Simple logic: If the last digit of the card number is even, the transaction is successful.
        // Otherwise, it's a failure.
        char lastDigit = paymentRequest.getCardNumber().charAt(paymentRequest.getCardNumber().length() - 1);
        return (lastDigit - '0') % 2 == 0;
    }
}
