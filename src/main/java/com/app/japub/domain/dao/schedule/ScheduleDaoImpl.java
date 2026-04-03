package com.app.japub.domain.dao.schedule;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.japub.domain.dto.ScheduleDto;
import com.app.japub.domain.mapper.ScheduleMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScheduleDaoImpl implements ScheduleDao {
	private final ScheduleMapper scheduleMapper;

	@Override
	public int insert(ScheduleDto scheduleDto) {
		return scheduleMapper.insert(scheduleDto);
	}

	@Override
	public int update(ScheduleDto scheduleDto) {
		return scheduleMapper.update(scheduleDto);
	}

	@Override
	public int deleteByScheduleNum(Long scheduleNum) {
		return scheduleMapper.deleteByScheduleNum(scheduleNum);
	}

	@Override
	public ScheduleDto findByScheduleNum(Long scheduleNum) {
		return scheduleMapper.findByScheduleNum(scheduleNum);
	}

	@Override
	public List<ScheduleDto> findByStartDateAndEndDate(String startDate, String endDate) {
		return scheduleMapper.findByStartDateAndEndDate(startDate, endDate);
	}

}
