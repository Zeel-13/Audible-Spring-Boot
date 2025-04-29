package com.audible.paymentMS.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.audible.paymentMS.exception.ResourceNotFoundException;
import com.audible.paymentMS.model.Payment;
import com.audible.paymentMS.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Override
    public String processPayment(Payment paymentRequest) {
        if (paymentRequest == null || paymentRequest.getOrderId() == null) {
            throw new IllegalArgumentException("Invalid payment request: payment or order ID is null");
        }

        try {
            // Simulate payment processing logic
            paymentRequest.setPaymentStatus("SUCCESS");
            paymentRequest.setTimestamp(LocalDateTime.now());

            paymentRepository.save(paymentRequest);
            return "Payment successful for Order ID: " + paymentRequest.getOrderId();

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while processing payment", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during payment processing", e);
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        try {
            List<Payment> payments = paymentRepository.findAll();
            if (payments.isEmpty()) {
                throw new ResourceNotFoundException("No payments found");
            }
            return payments;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while retrieving all payments", e);
        }
    }

    @Override
    public List<Payment> getPaymentsByUser(int userId) {
        try {
            List<Payment> payments = paymentRepository.findByUserId(userId);
            if (payments.isEmpty()) {
                throw new ResourceNotFoundException("No payments found for user ID: " + userId);
            }
            return payments;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while retrieving payments for user ID: " + userId, e);
        }
    }

    @Override
    public Optional<Payment> getPaymentByOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID must not be null or empty");
        }

        try {
            Optional<Payment> payment = paymentRepository.findByOrderId(orderId);
            if (payment.isEmpty()) {
                throw new ResourceNotFoundException("Payment not found for order ID: " + orderId);
            }
            return payment;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while retrieving payment for order ID: " + orderId, e);
        }
    }

}
