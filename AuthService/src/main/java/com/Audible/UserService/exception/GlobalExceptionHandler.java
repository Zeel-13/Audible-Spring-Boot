package com.Audible.UserService.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import feign.FeignException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<Object> handleFeignException(FeignException ex) {
		System.out.println("exception handled globally in auth service");
		return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
	}
}