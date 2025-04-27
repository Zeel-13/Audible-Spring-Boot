package com.audible.bookCartMS.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.audible.bookCartMS.dto.AudioBookDTO;
import com.audible.bookCartMS.feign.AudiobookClient;
import com.audible.bookCartMS.model.BookCart;
import com.audible.bookCartMS.model.Order;
import com.audible.bookCartMS.repository.BookCartRepository;

@Service
public class BookCartServiceImp {
	@Autowired
    private BookCartRepository bookCartRepository;
    
    @Autowired
    private AudiobookClient audiobookClient;
    
    public BookCart addBookCart(int userId, List<Integer> newAudiobookIds) {
        // Check if a BookCart already exists for this user
        Optional<BookCart> existingCartOpt = bookCartRepository.findByUserId(userId);

        if (existingCartOpt.isPresent()) {
            BookCart existingCart = existingCartOpt.get();
            List<Integer> currentIds = existingCart.getAudiobookIds();

            // Merge new IDs with existing, avoiding duplicates
            Set<Integer> mergedIds = new HashSet<>(currentIds);
            mergedIds.addAll(newAudiobookIds);

            existingCart.setAudiobookIds(new ArrayList<>(mergedIds));
            return bookCartRepository.save(existingCart);
        } else {
            // Create new cart if none exists
            BookCart newCart = new BookCart();
            newCart.setUserId(userId);
            newCart.setAudiobookIds(new ArrayList<>(newAudiobookIds));
            return bookCartRepository.save(newCart);
        }
    }
    
    // Remove specific audiobook
    public BookCart removeAudiobook(int userId, int audiobookId) {
        BookCart cart = bookCartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + userId));

        cart.getAudiobookIds().removeIf(id -> id == audiobookId);
        return bookCartRepository.save(cart);
    }
    
    // Clear cart
    public void clearCart(int userId) {
        BookCart cart = bookCartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + userId));
        cart.setAudiobookIds(new ArrayList<>());
        bookCartRepository.save(cart);
    }

    public ResponseEntity<List<AudioBookDTO>> getCartByUserId(Integer userId) {
        BookCart cart = bookCartRepository.findByUserId(userId).get();
        System.out.println("==================================================================");
        System.out.println(cart);
        List<Integer> bookIds = cart.getAudiobookIds();
        ResponseEntity<List<AudioBookDTO>> AudioBooks = audiobookClient.getAudiobooksByIds(bookIds);
        System.out.println("==================================================================");
        System.out.println("Audiobook IDs: " + bookIds);
        return AudioBooks;
    }

    public List<BookCart> getAllCarts() {
        return bookCartRepository.findAll();
    }

    public BookCart getCartByUserIdRaw(int userId) {
        return bookCartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + userId));
    }

}
