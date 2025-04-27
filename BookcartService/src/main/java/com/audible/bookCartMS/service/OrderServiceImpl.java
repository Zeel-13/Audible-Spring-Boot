package com.audible.bookCartMS.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.audible.bookCartMS.model.Order;
import com.audible.bookCartMS.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
    private OrderRepository orderRepository;

    @Override
    public Order placeOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUser(int userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

}
