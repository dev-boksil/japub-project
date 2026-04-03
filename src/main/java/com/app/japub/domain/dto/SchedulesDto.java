package com.app.japub.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulesDto {
    private List<ScheduleDto> schedules;
    private List<String> weekDates;
    private String today;
}
