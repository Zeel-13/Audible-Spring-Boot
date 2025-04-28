package com.Audible.UserService.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Audible.UserService.DTO.AudioBookDTO;
import com.Audible.UserService.DTO.BookCartDTO;
import com.Audible.UserService.DTO.OrderDTO;
import com.Audible.UserService.DTO.PaymentDTO;
import com.Audible.UserService.feign.AudioBookFeignClient;
import com.Audible.UserService.feign.BookCartFeignClient;
import com.Audible.UserService.feign.PaymentFeignClient;
import com.Audible.UserService.service.ServiceImpl;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private ServiceImpl service;

	@Autowired
	private PaymentFeignClient paymentClient;

	@Autowired
	private AudioBookFeignClient audiobookClient;

	@Autowired
	private BookCartFeignClient bookCartClient;

	@GetMapping("/all-audiobooks")
	public ResponseEntity<List<AudioBookDTO>> getAllAudioBooks() {
		return ResponseEntity.ok(audiobookClient.getAllAudioBooks());
	}

	@GetMapping("/audiobooks/{id}")
	public ResponseEntity<Object> getAudioBookById(@PathVariable int id) {
		Optional<AudioBookDTO> optional = audiobookClient.getAudioBookById(id);
		return ResponseEntity.ok(optional.get());
	}

	// Search by language
	@GetMapping("/audiobooks/search-by-language/{language}")
	public ResponseEntity<List<AudioBookDTO>> searchByLanguage(@PathVariable String language) {
		return ResponseEntity.ok(audiobookClient.searchByLanguage(language));
	}

	// Search by title
	@GetMapping("/audiobooks/search-by-title/{title}")
	public ResponseEntity<AudioBookDTO> searchByTitle(@PathVariable String title) {
		return ResponseEntity.ok(audiobookClient.searchByTitle(title));
	}

	/**
	 * POST /user/cart/add/{userId} Adds audiobooks to the user's cart. If cart
	 * doesn't exist, creates one.
	 */
	@PostMapping("/cart/add/{userId}")
	public ResponseEntity<BookCartDTO> addToCart(@PathVariable int userId, @RequestBody List<Integer> audiobookIds) {

		BookCartDTO savedCart = bookCartClient.addBookCart(userId, audiobookIds);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCart);
	}

	/**
	 * GET /user/cart/get/{userId} Returns list of audiobooks in the user's cart.
	 */
	@GetMapping("/cart/get/{userId}")
	public ResponseEntity<List<AudioBookDTO>> getUserCart(@PathVariable Integer userId) {
		return bookCartClient.getCartByUserId(userId);
	}

	/**
	 * DELETE /user/cart/remove/{userId}/{audiobookId} Removes a specific audiobook
	 * from the user's cart.
	 */
	@DeleteMapping("/cart/remove/{userId}/{audiobookId}")
	public ResponseEntity<BookCartDTO> removeAudiobookFromCart(@PathVariable int userId,
			@PathVariable int audiobookId) {

		BookCartDTO updatedCart = bookCartClient.removeAudiobook(userId, audiobookId);
		return ResponseEntity.ok(updatedCart);
	}

	/**
	 * DELETE /user/cart/clear/{userId} Clears all audiobooks from the user's cart.
	 */
	@DeleteMapping("/cart/clear/{userId}")
	public ResponseEntity<String> clearUserCart(@PathVariable int userId) {
		bookCartClient.clearCart(userId);
		return ResponseEntity.ok("Cart cleared successfully for user ID: " + userId);
	}

	// ****Orders
	@GetMapping("/orders/getByUserId/{userId}")
	public ResponseEntity<List<OrderDTO>> fetchOrdersByUserId(@PathVariable("userId") int userId) {
		return bookCartClient.getOrdersByUser(userId);
	}

	@GetMapping("/orders/getByOrderId/{orderId}")
	public ResponseEntity<OrderDTO> fetchOrderByOrderId(@PathVariable("orderId") String orderId) {
		return bookCartClient.getOrderById(orderId);
	}

	@PostMapping("/orders/place-order/{userId}/{mode}")
	public ResponseEntity<String> InitiateOrder(@PathVariable("userId") int userId, @PathVariable("mode") String mode) {
		return bookCartClient.placeOrder(userId, mode);
	}

	// ****Payments
	@GetMapping("/payments/getByUserId/{userId}")
	ResponseEntity<List<PaymentDTO>> fetchPaymentsByUserId(@PathVariable("userId") int userId) {
		return paymentClient.getPaymentsByUser(userId);
	}

	@GetMapping("/payments/getByOrderId/{orderId}")
	ResponseEntity<PaymentDTO> fetchPaymentByOrderId(@PathVariable("orderId") String orderId) {
		return paymentClient.getPaymentByOrderId(orderId);
	}
}
