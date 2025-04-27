package com.audible.paymentMS.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.audible.paymentMS.model.Payment;
import com.audible.paymentMS.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Override
	public String processPayment(Payment paymentRequest) {
		paymentRequest.setPaymentStatus("SUCCESS"); // Simulated logic
		paymentRequest.setTimestamp(LocalDateTime.now());
		paymentRepository.save(paymentRequest);
		return "Payment successful for Order ID: " + paymentRequest.getOrderId();
	}

	@Override
	public List<Payment> getAllPayments() {
		return paymentRepository.findAll();
	}

	@Override
	public List<Payment> getPaymentsByUser(int userId) {
		return paymentRepository.findByUserId(userId);
	}

	@Override
	public Optional<Payment> getPaymentByOrderId(String orderId) {
		return paymentRepository.findByOrderId(orderId);
	}

}
