package com.app.japub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class RestErrorController {
	// 400 계열(대표) - 상태코드 유지
	@ExceptionHandler({ org.springframework.http.converter.HttpMessageNotReadableException.class, // 요청 바디(HttpMessage)를
																									// 읽거나 파싱 못할 때
			org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class, // 파라미터(쿼리스트링/경로변수) 타입
																									// 변환 실패
			org.springframework.web.bind.MissingServletRequestParameterException.class }) // 필수 파라미터 누락
	public ResponseEntity<Void> badRequest(Exception e) {
		e.printStackTrace();
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Void> methodNotAllowed(HttpRequestMethodNotSupportedException e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Void> RestErrorhandler(Exception e) {
		e.printStackTrace();
		return ResponseEntity.internalServerError().build();
	}
}
