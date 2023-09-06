package com.example.paymentgateway.service;

import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.dto.PaymentResponse;
import com.example.paymentgateway.dto.PaymentDetailsResponse;

public interface PaymentProcessingService {

    PaymentResponse processPayment(PaymentRequest paymentRequest);

    PaymentDetailsResponse retrievePaymentDetails(String paymentId);
}