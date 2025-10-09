package com.app.japub.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {
	private static final String DEFAULT_ERROR_VIEW = "error/error";

	@GetMapping("/{errorCode}")
	public String errorHandler(@PathVariable int errorCode, HttpServletResponse resp) {
		resp.setStatus(errorCode);
		return DEFAULT_ERROR_VIEW;
	}
}
