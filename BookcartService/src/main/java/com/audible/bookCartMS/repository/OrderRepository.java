package com.audible.bookCartMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.audible.bookCartMS.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	List<Order> findByUserId(int userId);
}
