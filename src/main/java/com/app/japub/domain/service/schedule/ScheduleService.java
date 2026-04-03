package com.app.japub.domain.service.schedule;

import java.util.Calendar;
import java.util.List;

import com.app.japub.domain.dto.ScheduleDto;

public interface ScheduleService {
    public boolean insert(ScheduleDto scheduleDto);

    public boolean update(ScheduleDto scheduleDto);

    public boolean deleteByScheduleNum(Long scheduleNum);

    public String getStartDate(Calendar calendar, int page);

    public String getEndDate(Calendar calendar, int page);

    public List<String> getWeekDates(String startDate, String endDate);

    public List<ScheduleDto> findByStartDateAndEndDate(String startDate, String endDate);

    public ScheduleDto findByScheduleNum(Long scheduleNum);

}
