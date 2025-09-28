package com.app.japub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController {
	private static final String DEFAULT_ERROR_VIEW = "error/error";

	@GetMapping("/{errorCode}")
	public ModelAndView errorHandler(@PathVariable int errorCode) {
		ModelAndView modelAndView = null;
		if (errorCode == HttpStatus.BAD_REQUEST.value()) {
			modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW, HttpStatus.BAD_REQUEST);
		} else if (errorCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (errorCode == HttpStatus.NOT_FOUND.value()) {
			modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW, HttpStatus.NOT_FOUND);
		} else if (errorCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
			modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW, HttpStatus.METHOD_NOT_ALLOWED);
		} else if (errorCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
			modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		} else if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
			modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW, HttpStatus.UNAUTHORIZED);
		} else if (errorCode == HttpStatus.FORBIDDEN.value()) {
			modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW, HttpStatus.FORBIDDEN);
		}
		return modelAndView;
	}
}
