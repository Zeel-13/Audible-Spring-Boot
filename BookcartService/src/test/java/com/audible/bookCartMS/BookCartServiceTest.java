package com.audible.bookCartMS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.audible.bookCartMS.dto.AudioBookDTO;
import com.audible.bookCartMS.feign.AudiobookClient;
import com.audible.bookCartMS.model.BookCart;
import com.audible.bookCartMS.repository.BookCartRepository;
import com.audible.bookCartMS.service.BookCartServiceImp;

class BookCartServiceTest {

    @Mock
    private BookCartRepository bookCartRepository;

    @Mock
    private AudiobookClient audiobookClient;

    @InjectMocks
    private BookCartServiceImp bookCartServiceImp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddBookCart_NewCart() {
        int userId = 1;
        List<Integer> newAudiobookIds = Arrays.asList(101, 102);

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(bookCartRepository.save(any(BookCart.class))).thenAnswer(i -> i.getArguments()[0]);

        BookCart result = bookCartServiceImp.addBookCart(userId, newAudiobookIds);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAudiobookIds()).containsExactlyInAnyOrder(101, 102);
    }

    @Test
    void testAddBookCart_ExistingCart() {
        int userId = 1;
        List<Integer> existingIds = Arrays.asList(101);
        List<Integer> newAudiobookIds = Arrays.asList(102, 103);

        BookCart existingCart = new BookCart(1, userId, existingIds);

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.of(existingCart));
        when(bookCartRepository.save(any(BookCart.class))).thenAnswer(i -> i.getArguments()[0]);

        BookCart result = bookCartServiceImp.addBookCart(userId, newAudiobookIds);

        assertThat(result.getAudiobookIds()).containsExactlyInAnyOrder(101, 102, 103);
    }

    @Test
    void testRemoveAudiobook_Success() {
        int userId = 1;
        int audiobookId = 101;
        List<Integer> audiobookIds = new ArrayList<>(Arrays.asList(101, 102)); // <<< defensive copy

        BookCart cart = new BookCart(1, userId, audiobookIds);

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenAnswer(i -> i.getArguments()[0]);

        BookCart result = bookCartServiceImp.removeAudiobook(userId, audiobookId);

        assertThat(result.getAudiobookIds()).containsExactly(102);
    }


    @Test
    void testRemoveAudiobook_CartNotFound() {
        int userId = 1;

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookCartServiceImp.removeAudiobook(userId, 101);
        });

        assertThat(exception.getMessage()).contains("Cart not found for user ID");
    }

    @Test
    void testClearCart_Success() {
        int userId = 1;
        List<Integer> audiobookIds = Arrays.asList(101, 102);

        BookCart cart = new BookCart(1, userId, audiobookIds);

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenAnswer(i -> i.getArguments()[0]);

        bookCartServiceImp.clearCart(userId);

        assertThat(cart.getAudiobookIds()).isEmpty();
    }

    @Test
    void testClearCart_CartNotFound() {
        int userId = 1;

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookCartServiceImp.clearCart(userId);
        });

        assertThat(exception.getMessage()).contains("Cart not found for user ID");
    }

    @Test
    void testGetCartByUserId_Success() {
        int userId = 1;
        List<Integer> audiobookIds = Arrays.asList(101, 102);
        BookCart cart = new BookCart(1, userId, audiobookIds);

        List<AudioBookDTO> audiobookList = Arrays.asList(
            new AudioBookDTO(101, "Book1", "Author1","Narrator1",10,"Date1", "English",4.5,44.5,5),
            new AudioBookDTO(102, "Book2", "Author2","Narrator2",20,"Date2", "English",3.5,65.5,4)
        );

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(audiobookClient.getAudiobooksByIds(audiobookIds)).thenReturn(ResponseEntity.ok(audiobookList));

        ResponseEntity<List<AudioBookDTO>> response = bookCartServiceImp.getCartByUserId(userId);

        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Book1");
    }

    @Test
    void testGetAllCarts() {
        List<BookCart> carts = Arrays.asList(
            new BookCart(1, 1, Arrays.asList(101)),
            new BookCart(2, 2, Arrays.asList(102))
        );

        when(bookCartRepository.findAll()).thenReturn(carts);

        List<BookCart> result = bookCartServiceImp.getAllCarts();

        assertThat(result).hasSize(2);
    }

    @Test
    void testGetCartByUserIdRaw_Success() {
        int userId = 1;
        BookCart cart = new BookCart(1, userId, Arrays.asList(101));

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        BookCart result = bookCartServiceImp.getCartByUserIdRaw(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    void testGetCartByUserIdRaw_CartNotFound() {
        int userId = 1;

        when(bookCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookCartServiceImp.getCartByUserIdRaw(userId);
        });

        assertThat(exception.getMessage()).contains("Cart not found for user ID");
    }
}
