package com.app.japub.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.app.japub.domain.dto.ScheduleDto;

@Mapper
public interface ScheduleMapper {
	public int insert(ScheduleDto scheduleDto);

	public int update(ScheduleDto scheduleDto);

	public int deleteByScheduleNum(Long scheduleNum);

	public ScheduleDto findByScheduleNum(Long scheduleNum);

	public List<ScheduleDto> findByStartDateAndEndDate(@Param("startDate") String startDate,
			@Param("endDate") String endDate);
}
