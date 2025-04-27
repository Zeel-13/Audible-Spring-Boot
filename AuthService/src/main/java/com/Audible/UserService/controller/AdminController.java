package com.Audible.UserService.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Audible.UserService.DTO.AudioBookDTO;
import com.Audible.UserService.DTO.BookCartDTO;
import com.Audible.UserService.DTO.OrderDTO;
import com.Audible.UserService.DTO.PaymentDTO;
import com.Audible.UserService.entity.user;
import com.Audible.UserService.feign.AudioBookFeignClient;
import com.Audible.UserService.feign.BookCartFeignClient;
import com.Audible.UserService.feign.PaymentFeignClient;
import com.Audible.UserService.service.ServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ServiceImpl service;
    
    @Autowired
    private PaymentFeignClient paymentClient;
    
    @Autowired
    private AudioBookFeignClient audiobookClient;
    
    @Autowired
    private BookCartFeignClient bookCartClient;

    @PostMapping("/audiobook/add")
    public ResponseEntity<AudioBookDTO> addAudioBook(@RequestBody AudioBookDTO book) {
    	AudioBookDTO saved = audiobookClient.addAudioBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/audiobook/delete/{id}")
    public ResponseEntity<String> deleteAudioBook(@PathVariable int id) {
        String message = audiobookClient.deleteAudioBook(id);
        return ResponseEntity.ok(message);
    }
    
    @PutMapping("/audiobook/update-price/{bookId}")
    public ResponseEntity<AudioBookDTO> updateAudioBookPrice(@PathVariable int bookId, @RequestParam Double price) {
    	AudioBookDTO updatedBook = audiobookClient.updateAudioBookPrice(bookId, price);
        return ResponseEntity.ok(updatedBook);
    }

    @GetMapping("/getAllUsers")
	public List<user> getAllUsers() {
		return service.getAllUsers();
	}
	
	// Get user by username
    @GetMapping("/user/{username}")
    public user getUserByUsername(@PathVariable String username) {
        return service.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // Delete user by ID
    @DeleteMapping("/user/{customerId}")
    public void deleteUserById(@PathVariable Integer customerId) {
        service.deleteUserById(customerId);
    }

    // Update role
    @PutMapping("/user/update-role/{customerId}")
    public void updateAuthUserRole(@PathVariable Integer customerId, @RequestParam String role) {
        service.updateUserRole(customerId, role);
    }

    @GetMapping("/user/count")
    public ResponseEntity<Long> getUserCount() {
        long count = service.getUserCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/all/bookcarts")
    public List<BookCartDTO> fetchAllBookCarts() {
        return bookCartClient.getAllBookCarts();
    }
    
    //Orders
    @GetMapping("/orders/get-all")
    public ResponseEntity<List<OrderDTO>> fetchAllOrders(){
    	return bookCartClient.getAllOrders();
    }
    
    //Payments
    @GetMapping("/payments/get-all")
    public ResponseEntity<List<PaymentDTO>> fetchAllPayments(){
    	return paymentClient.getAllPayments();
    }
    
}
