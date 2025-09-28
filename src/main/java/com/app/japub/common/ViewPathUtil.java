package com.app.japub.common;

import com.app.japub.domain.dto.Criteria;

public class ViewPathUtil {
	public static final String REDIRECT_MAIN = "redirect:/main";
	public static final String REDIRECT_LOGIN = "redirect:/login";

	public static String getRedirectPath(Criteria criteria, String basePath, String subPath) {
		String params = criteria == null ? "" : criteria.getParams();
		return "redirect:/" + basePath + "/" + subPath + params;
	}

	public static String getForwardPath(String basePath, String subPath) {
		return basePath + "/" + subPath;
	}

	public static String getRedirectMainPath() {
		return "redirect:/main";
	}

	public static String getRedirectLoginPath() {
		return "redirect:/login";
	}
}
