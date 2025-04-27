package com.audible.bookCartMS.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audible.bookCartMS.dto.PaymentRequest;
import com.audible.bookCartMS.feign.PaymentClient;
import com.audible.bookCartMS.model.BookCart;
import com.audible.bookCartMS.model.Order;
import com.audible.bookCartMS.service.BookCartServiceImp;
import com.audible.bookCartMS.service.OrderServiceImpl;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private BookCartServiceImp bookCartService;

	@Autowired
	private OrderServiceImpl orderService;

	@Autowired
	private PaymentClient paymentClient;

	@PostMapping("/place/{userId}/{mode}")
	public ResponseEntity<String> placeOrder(@PathVariable int userId,@PathVariable String mode) {
		BookCart cart = bookCartService.getCartByUserIdRaw(userId);
		if (cart == null || cart.getAudiobookIds().isEmpty()) {
			return ResponseEntity.badRequest().body("Cart is empty or doesn't exist for user ID: " + userId);
		}

		double totalAmount = cart.getAudiobookIds().size() * 10.0;

		Order order = new Order(UUID.randomUUID().toString(), userId, cart.getAudiobookIds(), totalAmount);

		PaymentRequest paymentRequest = new PaymentRequest(order.getOrderId(), userId, totalAmount, mode);

		try {
			ResponseEntity<String> paymentResponse = paymentClient.makePayment(paymentRequest);
			if (paymentResponse.getStatusCode() == HttpStatus.OK) {
				orderService.placeOrder(order);
				bookCartService.clearCart(userId);
				return ResponseEntity.ok("Order placed and payment successful.");
			} else {
				return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed. Order not placed.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
			//"Payment service unavailable."
		}
	}
	
	
	//****USER
	// Get all orders for a user
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable int userId) {
	    List<Order> orders = orderService.getOrdersByUser(userId);
	    return ResponseEntity.ok(orders);
	}

	// Get specific order details by orderId
	@GetMapping("/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
	    Optional<Order> order = orderService.getOrderById(orderId);
	    return order.map(ResponseEntity::ok)
	                .orElse(ResponseEntity.notFound().build());
	}
	
	//****ADMIN
	// Get all orders (admin)
	@GetMapping("/all")
	public ResponseEntity<List<Order>> getAllOrders() {
	    return ResponseEntity.ok(orderService.getAllOrders());
	}


}
