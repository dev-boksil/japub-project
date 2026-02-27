package com.app.japub.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.japub.common.MessageConstants;
import com.app.japub.common.SessionUtil;
import com.app.japub.common.ViewPathUtil;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.dto.UserDto;
import com.app.japub.domain.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	private final UserService userService;
	private final HttpSession session;
	private static final String COOKIE_KEY = "id";
	private static final String BASE_PATH = "login";

	@GetMapping("/login")
	public String login(Criteria criteria) {
		if (SessionUtil.isLogin(session)) {
			return ViewPathUtil.REDIRECT_MAIN;
		}
		return ViewPathUtil.getForwardPath(BASE_PATH, BASE_PATH);
	}

	@PostMapping("/login")
	public String login(Criteria criteria, boolean rememberId, String userId, String userPassword, Long boardNum,
			Long productNum, HttpServletResponse resp, RedirectAttributes attributes, HttpServletRequest req) {
		if (SessionUtil.isLogin(session)) {
			return ViewPathUtil.REDIRECT_MAIN;
		}

		UserDto userDto = userService.login(userId, userPassword);

		if (userDto == null) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.LOGIN_ERROR_MSG);
			attributes.addAttribute("boardNum", boardNum);
			attributes.addAttribute("productNum", productNum);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, "");
		}

		SessionUtil.addUserNumToSession(session, userDto);
		SessionUtil.addIsAdminToSession(session, userDto);
		setCookie(rememberId, userId, resp);
		return getToUri(req, criteria, boardNum, productNum);
	}

	@GetMapping("/logout")
	public String logout() {
		if (SessionUtil.isLogin(session)) {
			session.invalidate();
		}
		return ViewPathUtil.REDIRECT_MAIN;
	}

	private void setCookie(boolean rememberId, String userId, HttpServletResponse resp) {
		Cookie cookie = new Cookie(COOKIE_KEY, rememberId ? userId : "");
		cookie.setPath("/");
		cookie.setMaxAge(rememberId ? 60 * 60 * 24 * 15 : 0);
		resp.addCookie(cookie);
	}

	private String getToUri(HttpServletRequest req, Criteria criteria, Long boardNum, Long productNum) {
		String toUri = criteria.getToUri();

		if (toUri == null || toUri.isEmpty()) {
			return ViewPathUtil.REDIRECT_MAIN;
		}

		String contextPath = req.getContextPath();

		if (contextPath != null && !contextPath.isEmpty() && toUri.startsWith(contextPath)) {
			toUri = toUri.substring(contextPath.length());
		}

		if (!toUri.startsWith("/")) {
			toUri = "/" + toUri;
		}

		StringBuilder builder = new StringBuilder();

		if (boardNum != null) {
			builder.append("&boardNum=").append(boardNum);
		}

		if (productNum != null) {
			builder.append("&productNum=").append(productNum);
		}

		return "redirect:" + toUri + criteria.getParams() + builder.toString();
	}

}
