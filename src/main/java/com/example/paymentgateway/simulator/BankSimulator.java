package com.example.paymentgateway.simulator;

import com.example.paymentgateway.dto.PaymentRequest;

public interface BankSimulator {

    boolean processTransaction(PaymentRequest paymentRequest);
}
