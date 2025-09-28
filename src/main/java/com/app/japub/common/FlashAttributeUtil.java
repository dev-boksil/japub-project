package com.app.japub.common;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class FlashAttributeUtil {
	public static final String SUCCESS_KEY = "success";
	public static final String SUCCESS_VALUE = "success";

	public static void addSuccessToFlash(RedirectAttributes attributes) {
		attributes.addFlashAttribute(SUCCESS_KEY, SUCCESS_VALUE);
	}

	public static boolean isSuccess(Model model) {
		return SUCCESS_VALUE.equals(model.getAttribute(SUCCESS_KEY));
	}

}
