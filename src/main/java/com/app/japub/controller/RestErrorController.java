package com.app.japub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class RestErrorController {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Void> RestErrorhandler(Exception e) {
		e.printStackTrace();
		return ResponseEntity.internalServerError().build();
	}
}
