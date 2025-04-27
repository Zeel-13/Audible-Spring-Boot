package com.audible.AudiobookService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.audible.AudiobookService.model.Audiobook;

public interface AudiobookService {
	
	public List<Audiobook> getAllAudioBooks();

	public Audiobook saveAudioBook(Audiobook book);

	Optional<Audiobook> getAudioBookById(int id);

	void deleteAudioBook(int id);

	List<Audiobook> getByLanguage(String language);

	Audiobook updateAudioBookPrice(int id, Double price);

	Optional<Audiobook> getByTitle(String title);
	

}
