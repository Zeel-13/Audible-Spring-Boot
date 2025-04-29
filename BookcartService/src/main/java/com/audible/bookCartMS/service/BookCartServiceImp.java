package com.audible.bookCartMS.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.audible.bookCartMS.dto.AudioBookDTO;
import com.audible.bookCartMS.exception.ResourceNotFoundException;
import com.audible.bookCartMS.feign.AudiobookClient;
import com.audible.bookCartMS.model.BookCart;
import com.audible.bookCartMS.repository.BookCartRepository;

import feign.FeignException;

@Service
public class BookCartServiceImp {
	@Autowired
    private BookCartRepository bookCartRepository;
    
    @Autowired
    private AudiobookClient audiobookClient;
    
    public BookCart addBookCart(int userId, List<Integer> newAudiobookIds) {
        if (newAudiobookIds == null || newAudiobookIds.isEmpty()) {
            throw new IllegalArgumentException("Audiobook ID list cannot be null or empty");
        }

        try {
            Optional<BookCart> existingCartOpt = bookCartRepository.findByUserId(userId);

            if (existingCartOpt.isPresent()) {
                BookCart existingCart = existingCartOpt.get();
                Set<Integer> mergedIds = new HashSet<>(existingCart.getAudiobookIds());
                mergedIds.addAll(newAudiobookIds);
                existingCart.setAudiobookIds(new ArrayList<>(mergedIds));
                return bookCartRepository.save(existingCart);
            } else {
                BookCart newCart = new BookCart();
                newCart.setUserId(userId);
                newCart.setAudiobookIds(new ArrayList<>(newAudiobookIds));
                return bookCartRepository.save(newCart);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while adding to book cart", e);
        }
    }

    public BookCart removeAudiobook(int userId, int audiobookId) {
        try {
            BookCart cart = bookCartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));

            boolean removed = cart.getAudiobookIds().removeIf(id -> id == audiobookId);
            if (!removed) {
                throw new ResourceNotFoundException("Audiobook ID " + audiobookId + " not found in cart");
            }
            return bookCartRepository.save(cart);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while removing audiobook from cart", e);
        }
    }

    public void clearCart(int userId) {
        try {
            BookCart cart = bookCartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));
            cart.setAudiobookIds(new ArrayList<>());
            bookCartRepository.save(cart);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while clearing book cart", e);
        }
    }

    public ResponseEntity<List<AudioBookDTO>> getCartByUserId(Integer userId) {
        try {
            BookCart cart = bookCartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));

            List<Integer> bookIds = cart.getAudiobookIds();

            if (bookIds == null || bookIds.isEmpty()) {
                throw new ResourceNotFoundException("No audiobooks found in cart for user ID: " + userId);
            }

            return audiobookClient.getAudiobooksByIds(bookIds);

        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Some audiobooks not found via Audiobook service");
        } catch (FeignException e) {
            throw new RuntimeException("Error fetching audiobook details from external service", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching book cart", e);
        }
    }

    public List<BookCart> getAllCarts() {
        try {
            return bookCartRepository.findAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching all carts", e);
        }
    }

    public BookCart getCartByUserIdRaw(int userId) {
        try {
            return bookCartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching cart", e);
        }
    }

}
