package com.app.japub.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.japub.common.FlashAttributeUtil;
import com.app.japub.common.MessageConstants;
import com.app.japub.common.SessionUtil;
import com.app.japub.common.ViewPathUtil;
import com.app.japub.domain.dto.UserDto;
import com.app.japub.domain.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/mypage")
@Controller
@RequiredArgsConstructor
public class MypageController {
	private final UserService userService;
	private final HttpSession session;
	private static final String BASE_PATH = "mypage";
	private static final String CHECK_PASSWORD_PATH = "check-password";
	private static final String UPDATE_PATH = "update";
	private static final String DELETE_PATH = "delete";
	private static final String KEY_USER = "user";

	@GetMapping("/check-password")
	public String checkPassword() {
		if (!SessionUtil.isLogin(session)) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		return ViewPathUtil.getForwardPath(BASE_PATH, CHECK_PASSWORD_PATH);
	}

	@PostMapping("/check-password")
	public String checkPassword(String userPassword, String isDelete, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		UserDto userDto = userService.findByUserNum(userNum);
		String redirectPath = redirectIfUserNotFound(userDto, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		boolean isSuccess = userService.findByUserNumAndUserPassword(userNum, userPassword) != null;
		if (!isSuccess) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.WRONG_PASSWORD_MSG);
			addDeleteToAttribute(parseBoolean(isDelete), attributes);
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, CHECK_PASSWORD_PATH);
		}
		FlashAttributeUtil.addSuccessToFlash(attributes);
		return ViewPathUtil.getRedirectPath(null, BASE_PATH, parseBoolean(isDelete) ? DELETE_PATH : UPDATE_PATH);
	}

	@GetMapping("/update")
	public String update(RedirectAttributes attributes, Model model, boolean isDelete) {
		return getUpdateOrDeleteView(attributes, model, false);
	}

	@GetMapping("/delete")
	public String delete(RedirectAttributes attributes, Model model, boolean isDelete) {
		return getUpdateOrDeleteView(attributes, model, true);
	}

	@PostMapping("/update")
	public String update(UserDto userDto, RedirectAttributes attributes) {
		return handleUpdateOrDelete(userDto, attributes, false);
	}

	@PostMapping("/delete")
	public String delete(UserDto userDto, RedirectAttributes attributes) {
		return handleUpdateOrDelete(userDto, attributes, true);
	}

	private String handleUpdateOrDelete(UserDto userDto, RedirectAttributes attributes, boolean isDelete) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		if (!SessionUtil.isSuccess(session)) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.INVALID_ACCESS_OR_EXPIRED_MSG);
			addDeleteToAttribute(isDelete, attributes);
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, CHECK_PASSWORD_PATH);
		}
		if (!userNum.equals(userDto.getUserNum())) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.PERMISSION_NOT_ALLOW_MSG);
			addDeleteToAttribute(isDelete, attributes);
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, CHECK_PASSWORD_PATH);
		}
		userDto.setUserNum(userNum);
		boolean isSuccess = isDelete ? userService.delete(userNum) : userService.update(userDto);
		if (!isSuccess) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			FlashAttributeUtil.addSuccessToFlash(attributes);
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, isDelete ? DELETE_PATH : UPDATE_PATH);
		}
		session.invalidate();
		MessageConstants.addSuccessMessage(attributes,
				isDelete ? MessageConstants.DELETE_ACCOUNT_MSG : MessageConstants.PASSWORD_UPDATE_SUCCESS_MESSAGE);
		return ViewPathUtil.REDIRECT_LOGIN;
	}

	private String getUpdateOrDeleteView(RedirectAttributes attributes, Model model, boolean isDelete) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		if (!FlashAttributeUtil.isSuccess(model)) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.INVALID_ACCESS_OR_EXPIRED_MSG);
			addDeleteToAttribute(isDelete, attributes);
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, CHECK_PASSWORD_PATH);
		}
		UserDto userDto = userService.findByUserNum(userNum);
		String redirectPath = redirectIfUserNotFound(userDto, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		model.addAttribute(KEY_USER, userDto);
		SessionUtil.addSuccess(session);
		return ViewPathUtil.getForwardPath(BASE_PATH, isDelete ? DELETE_PATH : UPDATE_PATH);
	}

	private String redirectIfUserNotFound(UserDto userDto, RedirectAttributes attributes) {
		if (userDto == null) {
			session.invalidate();
			MessageConstants.addErrorMessage(attributes, MessageConstants.USER_NOT_FOUND_MSG);
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		return null;
	}

	private void addDeleteToAttribute(boolean isDelete, RedirectAttributes attributes) {
		attributes.addAttribute("isDelete", isDelete);
	}

	private boolean parseBoolean(String isDelete) {
		return "true".equalsIgnoreCase(isDelete);
	}

}
