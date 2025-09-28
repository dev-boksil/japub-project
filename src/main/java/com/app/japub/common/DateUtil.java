package com.app.japub.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static String formatDateTime(String registerDate) {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(registerDate);
			return new SimpleDateFormat("yy-MM-dd HH:mm").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return registerDate;
		}
	}

	public static String formatDate(String registerDate) {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(registerDate);
			return new SimpleDateFormat("yy.MM.dd").format(date).toString();
		} catch (ParseException e) {
			e.printStackTrace();
			return registerDate;
		}
	}

	public static String getDatePath() {
		return new SimpleDateFormat("yyyy/MM/dd").format(new Date()).toString();
	}

	public static String getYesterDayPath() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime()).toString();
	}
}
