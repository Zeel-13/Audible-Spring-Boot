package com.audible.AudiobookService.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;
import com.audible.AudiobookService.service.AudiobookServiceImp;

import jakarta.validation.Valid;


@RestController
@Validated
@RequestMapping("/audiobooks")
public class AudiobookController {
	
	@Autowired
	private AudiobookServiceImp service;
	
	@GetMapping("/all")
	public ResponseEntity<List<Audiobook>> getAllAudioBooks(){
		return  ResponseEntity.ok(service.getAllAudioBooks());
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<Optional<Audiobook>> getAudioBookById(@PathVariable int id) {
        return ResponseEntity.ok(service.getAudioBookById(id));
    }

	@PostMapping("/add")
    public Audiobook addAudioBook(@Valid @RequestBody Audiobook book) {
        return service.saveAudioBook(book);
    }
	
	@DeleteMapping("/delete/{id}")
    public String deleteAudioBook(@PathVariable int id) {
        service.deleteAudioBook(id);
        return "Audiobook deleted successfully!";
    }
	
	@GetMapping("/search-by-language/{language}")
	public ResponseEntity<List<Audiobook>> searchByLanguage(@PathVariable String language){
		return  ResponseEntity.ok(service.getByLanguage(language)); 
	}
	
	@GetMapping("/search-by-title/{title}")
	public Optional<Audiobook> searchByTitle(@PathVariable String title){
		return service.getByTitle(title); 
	}
	
	@PutMapping("/update-price/{bookId}")
	public ResponseEntity<Audiobook> updateAudioBookPrice(
	        @PathVariable int bookId, 
	        @RequestParam Double price) {
//	    try {
	        Audiobook updatedBook = service.updateAudioBookPrice(bookId, price);
	        return ResponseEntity.ok(updatedBook); // Return 200 OK with the updated audiobook
//	    }  catch (Exception ex) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle other exceptions
//	    }
	}

	
	//Post mapping to give bookcart audiobooks
	@PostMapping("/list")
    public ResponseEntity<List<AudioBookDTO>> getAudioBookFromId(@RequestBody List<Integer> audiobookIds){
	    System.out.println("Received IDs: " + audiobookIds);
        return service.getAudioBookFromId(audiobookIds);
    }
}
