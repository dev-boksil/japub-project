package com.app.japub.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*@ControllerAdvice*/
public class ErrorControllerAdvice {
	@ExceptionHandler(Exception.class)
	public String exceptionHandler(Exception e, HttpServletRequest req, HttpServletResponse resp) {
		e.printStackTrace();
		Integer statusCode = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		statusCode = statusCode == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : statusCode;
		resp.setStatus(statusCode);
		return "error/error";
	}
}
