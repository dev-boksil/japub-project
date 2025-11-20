package com.app.japub.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.japub.common.FlashAttributeUtil;
import com.app.japub.common.MessageConstants;
import com.app.japub.common.SessionUtil;
import com.app.japub.common.ViewPathUtil;
import com.app.japub.domain.dto.UserDto;
import com.app.japub.domain.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/join")
@Controller
@RequiredArgsConstructor
public class JoinController {
	private final UserService userService;
	private final HttpSession session;
	private final static String KEY_USER = "user";
	private static final String BASE_PATH = "join";
	private static final String TERM_PATH = "term";

	@GetMapping("/term")
	public String term(RedirectAttributes attributes) {
		String redirectPath = redirectToMainIfLoggedIn(attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		return ViewPathUtil.getForwardPath(BASE_PATH, TERM_PATH);
	}

	@PostMapping("/term")
	public String termCheck(RedirectAttributes attributes) {
		String redirectPath = redirectToMainIfLoggedIn(attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		FlashAttributeUtil.addSuccessToFlash(attributes);
		return ViewPathUtil.getRedirectPath(null, BASE_PATH, "");
	}

	@GetMapping
	public String join(RedirectAttributes attributes, Model model) {
		String redirectPath = redirectToMainIfLoggedIn(attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		if (!FlashAttributeUtil.isSuccess(model)) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.INVALID_ACCESS_OR_EXPIRED_MSG);
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, TERM_PATH);
		}
		return ViewPathUtil.getForwardPath(BASE_PATH, BASE_PATH);
	}

	@PostMapping
	public String join(UserDto userDto, RedirectAttributes attributes) {
		String redirectPath = redirectToMainIfLoggedIn(attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		boolean isSuccess = userService.insert(userDto);
		if (!isSuccess) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			attributes.addFlashAttribute(KEY_USER, userDto);
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, "");
		}
		MessageConstants.addSuccessMessage(attributes, MessageConstants.JOIN_SUCCESS_MESSAGE);
		return ViewPathUtil.REDIRECT_LOGIN;
	}

	@PostMapping(value = "/checkId", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> checkId(@RequestBody UserDto userDto) {
		String userId = userDto.getUserId();
		if (userId == null) {
			return ResponseEntity.badRequest().build();
		}
		Boolean result = userService.findByUserId(userId) == null;
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

	@PostMapping(value = "/checkEmail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> checkEmail(@RequestBody UserDto userDto) {
		String userEmail = userDto.getUserEmail();
		if (userEmail == null) {
			return ResponseEntity.badRequest().build();
		}
		Boolean result = userService.findByUserEmail(userEmail) == null;
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

	private String redirectToMainIfLoggedIn(RedirectAttributes attributes) {
		if (SessionUtil.isLogin(session)) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ALREADY_LOGGED_IN_MSG);
			return ViewPathUtil.REDIRECT_MAIN;
		}
		return null;
	}
}
