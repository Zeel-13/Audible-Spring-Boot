package com.audible.AudiobookService.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.audible.AudiobookService.exception.ResourceNotFoundException;
import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;
import com.audible.AudiobookService.repository.AudiobookRepository;

@Service
public class AudiobookServiceImp implements AudiobookService {

	@Autowired
	private AudiobookRepository repository;
	
	@Override
	public List<Audiobook> getAllAudioBooks() {
		try {
            List<Audiobook> books = repository.findAll();
            if (books.isEmpty()) {
                throw new ResourceNotFoundException("No Audiobooks found");
            }
            return books;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching all audiobooks", e);
        }
	}

	@Override
	public Audiobook saveAudioBook(Audiobook book) {
		if (book == null) {
            throw new IllegalArgumentException("Audiobook cannot be null");
        }
        try {
            return repository.save(book);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while saving audiobook", e);
        }
    }

	@Override
	public Optional<Audiobook> getAudioBookById(int id) {
		try {
            Optional<Audiobook> book = repository.findById(id);
            if (book.isEmpty()) {
                throw new ResourceNotFoundException("Audiobook with ID " + id + " not found");
            }
            return book;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching audiobook by ID", e);
        }
	}

	@Override
	public void deleteAudioBook(int id) {
		try {
            if (!repository.existsById(id)) {
                throw new ResourceNotFoundException("Audiobook with ID " + id + " not found");
            }
            repository.deleteById(id);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while deleting audiobook", e);
        }
	}

	@Override
	public Audiobook updateAudioBookPrice(int bookId, Double price) {
		if (price == null || price <= 0) {
            throw new IllegalArgumentException("Invalid price value");
        }
        try {
            Optional<Audiobook> optionalBook = repository.findById(bookId);
            if (optionalBook.isPresent()) {
                Audiobook existingBook = optionalBook.get();
                existingBook.setPrice(price);
                return repository.save(existingBook);
            } else {
                throw new ResourceNotFoundException("Audiobook with ID " + bookId + " not found");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while updating audiobook price", e);
        }
	}

	public List<Audiobook> getByLanguage(String language) {
		if (language == null || language.trim().isEmpty()) {
            throw new IllegalArgumentException("Language cannot be null or empty");
        }
        try {
            return repository.findAllByLanguage(language);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching audiobooks by language", e);
        }
	}
	
	public ResponseEntity<List<AudioBookDTO>> getAudioBookFromId(List<Integer> audiobookIds) {
		if (audiobookIds == null || audiobookIds.isEmpty()) {
            throw new IllegalArgumentException("Audiobook ID list cannot be null or empty");
        }

        List<AudioBookDTO> audiobooksdto = new ArrayList<>();

        try {
            for (Integer id : audiobookIds) {
                Audiobook audiobook = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Audiobook with ID " + id + " not found"));

                AudioBookDTO wrapper = new AudioBookDTO();
                wrapper.setBookId(audiobook.getBookId());
                wrapper.setTitle(audiobook.getTitle());
                wrapper.setAuthor(audiobook.getAuthor());
                wrapper.setNarrator(audiobook.getNarrator());
                wrapper.setPrice(audiobook.getPrice());
                wrapper.setTime(audiobook.getTime());
                wrapper.setRelease_date(audiobook.getRelease_date());
                wrapper.setLanguage(audiobook.getLanguage());
                wrapper.setStars(audiobook.getStars());
                wrapper.setRatings(audiobook.getRatings());

                audiobooksdto.add(wrapper);
            }

            return new ResponseEntity<>(audiobooksdto, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching audiobooks from IDs", e);
        }
	}


	@Override
	public Optional<Audiobook> getByTitle(String title) {
		if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        try {
            Optional<Audiobook> book = repository.findByTitle(title);
            if (book.isEmpty()) {
                throw new ResourceNotFoundException("Audiobook with title '" + title + "' not found");
            }
            return book;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching audiobook by title", e);
        }
	}

}
