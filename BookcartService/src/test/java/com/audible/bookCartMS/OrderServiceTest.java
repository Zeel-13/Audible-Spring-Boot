package com.audible.bookCartMS;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.audible.bookCartMS.model.Order;
import com.audible.bookCartMS.repository.OrderRepository;
import com.audible.bookCartMS.service.OrderServiceImpl;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlaceOrder_Success() {
        Order order = new Order("order123", 1, Arrays.asList(101, 102), 49.99);

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderServiceImpl.placeOrder(order);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo("order123");
        assertThat(result.getTotalAmount()).isEqualTo(49.99);
    }

    @Test
    void testGetAllOrders_Success() {
        List<Order> orders = Arrays.asList(
            new Order("order1", 1, Arrays.asList(101), 19.99),
            new Order("order2", 2, Arrays.asList(102), 29.99)
        );

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderServiceImpl.getAllOrders();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOrderId()).isEqualTo("order1");
    }

    @Test
    void testGetOrdersByUser_Success() {
        int userId = 1;
        List<Order> userOrders = Arrays.asList(
            new Order("order1", userId, Arrays.asList(101), 19.99),
            new Order("order3", userId, Arrays.asList(103), 39.99)
        );

        when(orderRepository.findByUserId(userId)).thenReturn(userOrders);

        List<Order> result = orderServiceImpl.getOrdersByUser(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    void testGetOrderById_Success() {
        String orderId = "order123";
        Order order = new Order(orderId, 1, Arrays.asList(101, 102), 49.99);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderServiceImpl.getOrderById(orderId);

        assertThat(result).isPresent();
        assertThat(result.get().getOrderId()).isEqualTo(orderId);
    }

    @Test
    void testGetOrderById_NotFound() {
        String orderId = "nonexistent";

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = orderServiceImpl.getOrderById(orderId);

        assertThat(result).isEmpty();
    }
}
