package com.app.japub.domain.dao.schedule;

import java.util.List;

import com.app.japub.domain.dto.ScheduleDto;

public interface ScheduleDao {
	public int insert(ScheduleDto scheduleDto);

	public int update(ScheduleDto scheduleDto);

	public int deleteByScheduleNum(Long scheduleNum);

	public ScheduleDto findByScheduleNum(Long scheduleNum);

	public List<ScheduleDto> findByStartDateAndEndDate(String startDate, String endDate);
}
