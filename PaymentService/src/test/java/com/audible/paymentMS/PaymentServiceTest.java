package com.audible.paymentMS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.audible.paymentMS.model.Payment;
import com.audible.paymentMS.repository.PaymentRepository;
import com.audible.paymentMS.service.PaymentServiceImpl;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPayment_Success() {
        Payment paymentRequest = new Payment(0, "order123", 1, 99.99, "CARD", null, null);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        String result = paymentServiceImpl.processPayment(paymentRequest);

        assertThat(result).isEqualTo("Payment successful for Order ID: order123");
        assertThat(paymentRequest.getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(paymentRequest.getTimestamp()).isNotNull();
        verify(paymentRepository, times(1)).save(paymentRequest);
    }

    @Test
    void testGetAllPayments_Success() {
        List<Payment> payments = Arrays.asList(
            new Payment(1, "order1", 1, 49.99, "UPI", "SUCCESS", LocalDateTime.now()),
            new Payment(2, "order2", 2, 29.99, "CARD", "SUCCESS", LocalDateTime.now())
        );

        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentServiceImpl.getAllPayments();

        assertThat(result).hasSize(2);
    }

    @Test
    void testGetPaymentsByUser_Success() {
        int userId = 1;
        List<Payment> userPayments = Arrays.asList(
            new Payment(1, "order1", userId, 49.99, "UPI", "SUCCESS", LocalDateTime.now()),
            new Payment(3, "order3", userId, 19.99, "CARD", "SUCCESS", LocalDateTime.now())
        );

        when(paymentRepository.findByUserId(userId)).thenReturn(userPayments);

        List<Payment> result = paymentServiceImpl.getPaymentsByUser(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    void testGetPaymentByOrderId_Success() {
        String orderId = "order123";
        Payment payment = new Payment(1, orderId, 1, 99.99, "CARD", "SUCCESS", LocalDateTime.now());

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentServiceImpl.getPaymentByOrderId(orderId);

        assertThat(result).isPresent();
        assertThat(result.get().getOrderId()).isEqualTo(orderId);
    }

    @Test
    void testGetPaymentByOrderId_NotFound() {
        String orderId = "nonexistent";

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentServiceImpl.getPaymentByOrderId(orderId);

        assertThat(result).isEmpty();
    }
}


