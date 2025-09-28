package com.app.japub.common;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.app.japub.domain.dto.UserDto;

public class SessionUtil {

	public static final String KEY = "userNum";
	public static final String ADMIN_KEY = "isAdmin";
	public static final String SUCCESS_KEY = "success";
	public static final String SUCCESS_VALUE = "success";
	public static final Long TTL = 120 * 1000L; // 2분

	public static Long getSessionNum(HttpSession session) {
		return (Long) session.getAttribute(KEY);
	}

	public static boolean isAdmin(HttpSession session) {
		Boolean isAdmin = (Boolean) session.getAttribute(ADMIN_KEY);
		return Boolean.TRUE.equals(isAdmin);
	}

	public static void addIsAdminToModel(Model model, HttpSession session) {
		model.addAttribute(ADMIN_KEY, isAdmin(session));
	}

	public static void addUserNumToSession(HttpSession session, UserDto userDto) {
		session.setAttribute(KEY, userDto.getUserNum());
	}

	public static void addIsAdminToSession(HttpSession session, UserDto userDto) {
		session.setAttribute(ADMIN_KEY, userDto.isUserRole());
	}

	public static boolean isLogin(HttpSession session) {
		return session.getAttribute(KEY) != null;
	}

	public static void addSuccess(HttpSession session) {
		session.setAttribute(SUCCESS_KEY, System.currentTimeMillis());
	}

	public static boolean checkSuccess(HttpSession session) {
		Long time = (Long) session.getAttribute(SUCCESS_KEY);
		if (time == null) {
			return false;
		}
		Long gap = System.currentTimeMillis() - time;
		if (gap < 0 || gap >= TTL) {
			session.removeAttribute(SUCCESS_KEY);
			return false;
		}
		return true;
	}

	public static boolean isSuccess(HttpSession session) {
		if (!checkSuccess(session)) {
			return false;
		}
		session.removeAttribute(SUCCESS_KEY);
		return true;
	}

}
