package com.audible.AudiobookService.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
		List<Audiobook> book = repository.findAll();
	    if (book.isEmpty()) {
	        throw new ResourceNotFoundException("No Audiobook found");
	    }
	    return book;
	}

	@Override
	public Audiobook saveAudioBook(Audiobook book) {
        return repository.save(book);
    }

	@Override
	public Optional<Audiobook> getAudioBookById(int id) {
	    Optional<Audiobook> book = repository.findById(id);
	    if (book.isEmpty()) {
	        throw new ResourceNotFoundException("Audiobook with ID " + id + " not found");
	    }
	    return book;
	}

	@Override
	public void deleteAudioBook(int id) {
	    if (!repository.existsById(id)) {
	        throw new ResourceNotFoundException("Audiobook with ID " + id + " not found");
	    }
	    repository.deleteById(id);
	}

	@Override
	public Audiobook updateAudioBookPrice(int bookId, Double price) {
	    Optional<Audiobook> optionalBook = repository.findById(bookId);
	    if (optionalBook.isPresent()) {
	        Audiobook existingBook = optionalBook.get();
	        existingBook.setPrice(price);
	        return repository.save(existingBook);
	    } else {
	        throw new ResourceNotFoundException("Audiobook with ID " + bookId + " not found");
	    }
	}

	public List<Audiobook> getByLanguage(String language) {
		return repository.findAllByLanguage(language);
	}
	
	public ResponseEntity<List<AudioBookDTO>> getAudioBookFromId(List<Integer> audiobookIds) {
	    List<AudioBookDTO> audiobooksdto = new ArrayList<>();

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
	}


	@Override
	public Optional<Audiobook> getByTitle(String title) {
		Optional<Audiobook> book = repository.findByTitle(title);
	    if (book.isEmpty()) {
	        throw new ResourceNotFoundException("Audiobook with title " + title + " not found");
	    }
	    return book;
	}

}
