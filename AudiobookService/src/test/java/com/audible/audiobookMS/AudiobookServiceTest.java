package com.audible.audiobookMS;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.audible.AudiobookService.exception.ResourceNotFoundException;
import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;
import com.audible.AudiobookService.repository.AudiobookRepository;
import com.audible.AudiobookService.service.AudiobookServiceImp;

class AudiobookServiceTest {

    @Mock
    private AudiobookRepository repository;

    @InjectMocks
    private AudiobookServiceImp service;

    private Audiobook sampleBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleBook = new Audiobook();
        sampleBook.setBookId(1);
        sampleBook.setTitle("Test Book");
        sampleBook.setAuthor("Author");
        sampleBook.setNarrator("Narrator");
        sampleBook.setLanguage("English");
        sampleBook.setPrice(19.99);
        sampleBook.setRatings(100);
        sampleBook.setStars(4.5);
        sampleBook.setTime(10);
        sampleBook.setRelease_date("2022-01-01");
    }

    @Test
    void getAllAudioBooks_ShouldReturnBooks() {
        when(repository.findAll()).thenReturn(Collections.singletonList(sampleBook));
        List<Audiobook> books = service.getAllAudioBooks();
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
    }

    @Test
    void getAllAudioBooks_ShouldThrow_WhenEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> service.getAllAudioBooks());
    }

    @Test
    void saveAudioBook_ShouldReturnSavedBook() {
        when(repository.save(sampleBook)).thenReturn(sampleBook);
        Audiobook result = service.saveAudioBook(sampleBook);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void saveAudioBook_ShouldThrow_WhenNull() {
        assertThrows(IllegalArgumentException.class, () -> service.saveAudioBook(null));
    }

    @Test
    void getAudioBookById_ShouldReturnBook() {
        when(repository.findById(1)).thenReturn(Optional.of(sampleBook));
        Optional<Audiobook> result = service.getAudioBookById(1);
        assertTrue(result.isPresent());
    }

    @Test
    void getAudioBookById_ShouldThrow_WhenNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getAudioBookById(1));
    }

    @Test
    void deleteAudioBook_ShouldDeleteBook() {
        when(repository.existsById(1)).thenReturn(true);
        doNothing().when(repository).deleteById(1);
        assertDoesNotThrow(() -> service.deleteAudioBook(1));
    }

    @Test
    void deleteAudioBook_ShouldThrow_WhenNotFound() {
        when(repository.existsById(1)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteAudioBook(1));
    }

    @Test
    void updateAudioBookPrice_ShouldUpdatePrice() {
        when(repository.findById(1)).thenReturn(Optional.of(sampleBook));
        when(repository.save(any(Audiobook.class))).thenReturn(sampleBook);

        Audiobook updated = service.updateAudioBookPrice(1, 29.99);
        assertEquals(29.99, updated.getPrice());
    }

    @Test
    void updateAudioBookPrice_ShouldThrow_WhenPriceInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.updateAudioBookPrice(1, -10.0));
    }

    @Test
    void updateAudioBookPrice_ShouldThrow_WhenNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateAudioBookPrice(1, 25.0));
    }

    @Test
    void getByLanguage_ShouldReturnList() {
        when(repository.findAllByLanguage("English")).thenReturn(List.of(sampleBook));
        List<Audiobook> result = service.getByLanguage("English");
        assertEquals(1, result.size());
    }

    @Test
    void getByLanguage_ShouldThrow_WhenInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.getByLanguage(""));
    }

    @Test
    void getAudioBookFromId_ShouldReturnDTOs() {
        when(repository.findById(1)).thenReturn(Optional.of(sampleBook));
        List<AudioBookDTO> result = service.getAudioBookFromId(List.of(1)).getBody();
        assertNotNull(result);
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    void getAudioBookFromId_ShouldThrow_WhenIdListEmpty() {
        assertThrows(IllegalArgumentException.class, () -> service.getAudioBookFromId(Collections.emptyList()));
    }

    @Test
    void getByTitle_ShouldReturnBook() {
        when(repository.findByTitle("Test Book")).thenReturn(Optional.of(sampleBook));
        Optional<Audiobook> result = service.getByTitle("Test Book");
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
    }

    @Test
    void getByTitle_ShouldThrow_WhenNotFound() {
        when(repository.findByTitle("Unknown")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getByTitle("Unknown"));
    }
}
