package com.example.paymentgateway.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * Find a payment by its idempotency key.
     *
     * @param idempotencyKey the idempotency key
     * @return an Optional containing the found payment, or empty if not found
     */
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
