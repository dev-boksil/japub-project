package com.app.japub.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.japub.common.MessageConstants;
import com.app.japub.common.SessionUtil;
import com.app.japub.common.ViewPathUtil;
import com.app.japub.domain.dto.UserDto;
import com.app.japub.domain.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/find-account")
@RequiredArgsConstructor
@Controller
public class FindAccountController {
	private final UserService userService;
	private final HttpSession session;

	@GetMapping()
	public String findAccount(RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum != null) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ALREADY_LOGGED_IN_MSG);
			return ViewPathUtil.getRedirectMainPath();
		}
		return "find/find-account";
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> findAccount(@RequestBody UserDto userDto) {
		String email = userDto.getUserEmail();
		if (email == null) {
			return new ResponseEntity<String>(MessageConstants.ERROR_MSG, HttpStatus.BAD_REQUEST);
		}
		UserDto userToValidate = userService.findByUserEmail(userDto.getUserEmail());
		if (userToValidate == null) {
			return new ResponseEntity<String>(MessageConstants.USER_NOT_FOUND_MSG, HttpStatus.NOT_FOUND);
		}
		try {
			userService.setUserPasswordAndSendMail(userToValidate);
			return new ResponseEntity<String>("입력하신 이메일로 아이디와 임시 비밀번호를 전송하였습니다.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("메일 전송중 오류가 발생하였습니다 잠시후 다시 시도해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
