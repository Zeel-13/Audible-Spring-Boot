package com.audible.AudiobookService.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.audible.AudiobookService.model.Audiobook;

@Repository
public interface AudiobookRepository extends JpaRepository<Audiobook, Integer> {

	List<Audiobook> findAllByLanguage(String language);

	Optional<Audiobook> findByTitle(String title);

}
