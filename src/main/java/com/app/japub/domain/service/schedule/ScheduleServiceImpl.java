package com.app.japub.domain.service.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.japub.common.DbConstants;
import com.app.japub.domain.dao.schedule.ScheduleDao;
import com.app.japub.domain.dto.ScheduleDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
	private final ScheduleDao scheduleDao;

	@Override
	public boolean insert(ScheduleDto scheduleDto) {
		return scheduleDao.insert(scheduleDto) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public boolean update(ScheduleDto scheduleDto) {
		return scheduleDao.update(scheduleDto) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public boolean deleteByScheduleNum(Long scheduleNum) {
		return scheduleDao.deleteByScheduleNum(scheduleNum) == DbConstants.SUCCESS_CODE;
	}

	public String getStartDate(Calendar calendar, int page) { // 월요일2 - 현재요일뺀거를 현재날짜에서 add 해주면 항상 월요일
		calendar = (Calendar) calendar.clone();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 요일
		if (dayOfWeek == Calendar.SUNDAY) {
			calendar.add(Calendar.DATE, -6);
		} else {
			calendar.add(Calendar.DATE, Calendar.MONDAY - dayOfWeek);
		}
		calendar.add(Calendar.DATE, 7 * page);
		return toString(calendar);
	}

	public String getEndDate(Calendar calendar, int page) {
		calendar = (Calendar) calendar.clone();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek != Calendar.SUNDAY) {
			calendar.add(Calendar.DATE, 8 - dayOfWeek);
		}

		calendar.add(Calendar.DATE, 7 * page);
		return toString(calendar);
	}

	public List<String> getWeekDates(String startDate, String endDate) {
		List<String> dates = new ArrayList<>();
		Calendar currentDate = parse(startDate);

		while (!currentDate.after(parse(endDate))) {
			dates.add(toString(currentDate));
			currentDate.add(Calendar.DATE, 1);
		}
		return dates;
	}

	@Override
	public List<ScheduleDto> findByStartDateAndEndDate(String startDate, String endDate) {
		List<ScheduleDto> schedules = scheduleDao.findByStartDateAndEndDate(startDate, endDate);

		// js에서 html 속 data속성이랑 비교하려면 초단위는 제거해줘야함 db에서 datetime 타입 가져올땐 무조건 초단위까지 가져옴
		schedules.forEach(scheduleDto -> {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(scheduleDto.getScheduleReservationDate());
				scheduleDto.setScheduleReservationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));
			} catch (ParseException e) {
				throw new RuntimeException("scheduleService findByStartDateAndEndDate ParseException error", e);
			}
		});

		return schedules;
	}

	@Override
	public ScheduleDto findByScheduleNum(Long scheduleNum) {
		return scheduleDao.findByScheduleNum(scheduleNum);
	}

	public String toString(Calendar calendar) {
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}

	public Calendar parse(String date) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
			return calendar;
		} catch (ParseException e) {
			throw new RuntimeException("scheduleService parse error", e);
		}
	}
}
