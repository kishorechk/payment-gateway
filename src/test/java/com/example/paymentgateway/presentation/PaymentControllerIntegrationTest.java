package com.example.paymentgateway.presentation;

import com.example.paymentgateway.dto.PaymentDetailsResponse;
import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.dto.PaymentResponse;
import com.example.paymentgateway.service.PaymentProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentProcessingService paymentProcessingService;

    @Test
    public void testProcessPaymentSuccess() throws Exception {
        PaymentRequest request = new PaymentRequest("4111111111111112", "12", "2025", "123", 100.50, "USD", UUID.randomUUID().toString());
        var response = PaymentResponse.builder().paymentId("1").status("SUCCESS").build();
        when(paymentProcessingService.processPayment(any(PaymentRequest.class))).thenReturn(response);

        var idempotencyKey = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", UUID.randomUUID().toString())  // Add this line
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testProcessPayment_IdempotencyValidation() throws Exception {
        PaymentRequest request = new PaymentRequest("4111111111111112", "12", "2025", "123", 100.50, "USD", UUID.randomUUID().toString());
        String idempotencyKey = UUID.randomUUID().toString();

        var response = PaymentResponse.builder().paymentId("1").status("SUCCESS").build();
        when(paymentProcessingService.processPayment(any(PaymentRequest.class))).thenReturn(response);

        // First request with the idempotency key
        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        // Second request with the same idempotency key
        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testProcessPaymentFailure() throws Exception {
        PaymentRequest request = new PaymentRequest();
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testProcessPayment_MissingCardNumber() throws Exception {
        PaymentRequest request = new PaymentRequest(null, "12", "2025", "123", 100.0, "USD", UUID.randomUUID().toString());
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.cardNumber").value("Card number is required."));
    }

    @Test
    public void testProcessPayment_InvalidExpiryDate() throws Exception {
        PaymentRequest request = new PaymentRequest("4111111111111111", "13", "2020", "123", 100.0, "USD", UUID.randomUUID().toString());
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.expiryMonth").value("Expiry month should be MM format."));
    }

    @Test
    public void testProcessPayment_MissingCVV() throws Exception {
        PaymentRequest request = new PaymentRequest("4111111111111111", "12", "2025", null, 100.0, "USD", UUID.randomUUID().toString());
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.cvv").value("CVV is required."));
    }

    @Test
    public void testProcessPayment_InvalidAmount() throws Exception {
        PaymentRequest request = new PaymentRequest("4111111111111111", "12", "2025", "123", -10.0, "USD", UUID.randomUUID().toString());
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.amount").value("Amount should be positive."));
    }

    @Test
    public void testProcessPayment_MissingIdempotencyKey() throws Exception {
        PaymentRequest request = new PaymentRequest("4111111111111111", "12", "2025", "123", 10.0, "USD", null);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.idempotencyKey").value("Idempotency Key is required."));
    }

    @Test
    public void testRetrievePaymentDetails_Success() throws Exception {
        String paymentId = "1";
        PaymentDetailsResponse mockResponse = new PaymentDetailsResponse();
        mockResponse.setPaymentId(paymentId);
        mockResponse.setStatus("SUCCESS");
        when(paymentProcessingService.retrievePaymentDetails(anyString())).thenReturn(mockResponse);
        mockMvc.perform(get("/api/payments/" + paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testRetrievePaymentDetails_NotFound() throws Exception {
        String paymentId = "1";

        when(paymentProcessingService.retrievePaymentDetails(paymentId)).thenThrow(new EntityNotFoundException("Payment with ID " + paymentId + " not found."));

        mockMvc.perform(get("/api/payments/" + paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Payment with ID " + paymentId + " not found."));
    }

}
