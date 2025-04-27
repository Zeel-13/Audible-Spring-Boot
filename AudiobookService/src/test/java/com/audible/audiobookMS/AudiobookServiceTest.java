package com.audible.audiobookMS;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.audible.AudiobookService.exception.ResourceNotFoundException;
import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;
import com.audible.AudiobookService.repository.AudiobookRepository;
import com.audible.AudiobookService.service.AudiobookServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

class AudiobookServiceTest {

    @Mock
    private AudiobookRepository repository;

    @InjectMocks
    private AudiobookServiceImp service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAudioBooks_ReturnsList() {
        List<Audiobook> books = Arrays.asList(new Audiobook(), new Audiobook());
        when(repository.findAll()).thenReturn(books);

        List<Audiobook> result = service.getAllAudioBooks();

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllAudioBooks_ThrowsException() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> service.getAllAudioBooks());
    }

    @Test
    void testSaveAudioBook() {
        Audiobook book = new Audiobook();
        when(repository.save(book)).thenReturn(book);

        Audiobook result = service.saveAudioBook(book);

        assertNotNull(result);
        verify(repository).save(book);
    }

    @Test
    void testGetAudioBookById_Found() {
        Audiobook book = new Audiobook();
        when(repository.findById(1)).thenReturn(Optional.of(book));

        Optional<Audiobook> result = service.getAudioBookById(1);

        assertTrue(result.isPresent());
    }

    @Test
    void testGetAudioBookById_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getAudioBookById(1));
    }

    @Test
    void testDeleteAudioBook_Found() {
        when(repository.existsById(1)).thenReturn(true);

        service.deleteAudioBook(1);

        verify(repository).deleteById(1);
    }

    @Test
    void testDeleteAudioBook_NotFound() {
        when(repository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteAudioBook(1));
    }

    @Test
    void testUpdateAudioBookPrice_Found() {
        Audiobook book = new Audiobook();
        book.setPrice(10.0);
        when(repository.findById(1)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);

        Audiobook updated = service.updateAudioBookPrice(1, 20.0);

        assertEquals(20.0, updated.getPrice());
    }

    @Test
    void testUpdateAudioBookPrice_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateAudioBookPrice(1, 20.0));
    }

    @Test
    void testGetByLanguage() {
        List<Audiobook> books = Arrays.asList(new Audiobook());
        when(repository.findAllByLanguage("English")).thenReturn(books);

        List<Audiobook> result = service.getByLanguage("English");

        assertEquals(1, result.size());
    }

    @Test
    void testGetAudioBookFromId_Found() {
        Audiobook book = new Audiobook();
        book.setBookId(1);
        book.setTitle("Test");
        book.setAuthor("Author");
        book.setNarrator("Narrator");
        book.setPrice(25.0);

        when(repository.findById(1)).thenReturn(Optional.of(book));

        List<Integer> ids = Arrays.asList(1);
        ResponseEntity<List<AudioBookDTO>> response = service.getAudioBookFromId(ids);

        assertEquals(1, response.getBody().size());
        assertEquals("Test", response.getBody().get(0).getTitle());
    }

    @Test
    void testGetAudioBookFromId_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getAudioBookFromId(Arrays.asList(1)));
    }

    @Test
    void testGetByTitle_Found() {
        Audiobook book = new Audiobook();
        book.setTitle("Test");
        when(repository.findByTitle("Test")).thenReturn(Optional.of(book));

        Optional<Audiobook> result = service.getByTitle("Test");

        assertTrue(result.isPresent());
    }

    @Test
    void testGetByTitle_NotFound() {
        when(repository.findByTitle("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getByTitle("Unknown"));
    }
}

