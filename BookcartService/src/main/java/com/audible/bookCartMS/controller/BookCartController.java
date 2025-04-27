package com.audible.bookCartMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audible.bookCartMS.dto.AudioBookDTO;
import com.audible.bookCartMS.model.BookCart;
import com.audible.bookCartMS.service.BookCartServiceImp;

@RestController
@RequestMapping("/cart")
public class BookCartController {

    @Autowired
    private BookCartServiceImp bookCartService;
    
    @PostMapping("/add/{userId}")
    public ResponseEntity<BookCart> addBookCart(
    	@PathVariable int userId, 
        @RequestBody List<Integer> audiobookIds) {
        
        BookCart savedCart = bookCartService.addBookCart(userId, audiobookIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<AudioBookDTO>> getCartAudiobooks(@PathVariable Integer userId) {
        return bookCartService.getCartByUserId(userId);
    }
    
    
    @DeleteMapping("/remove/{userId}/{audiobookId}")
    public ResponseEntity<BookCart> removeAudiobook(
        @PathVariable int userId, 
        @PathVariable int audiobookId) {
        
        BookCart updatedCart = bookCartService.removeAudiobook(userId, audiobookId);
        return ResponseEntity.ok(updatedCart);
    }
    
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable int userId) {
        bookCartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully for user ID: " + userId);
    }
    
    @GetMapping("/all-carts")
    public ResponseEntity<List<BookCart>> getAllCarts() {
        List<BookCart> carts = bookCartService.getAllCarts();
        return ResponseEntity.ok(carts);
    }
}