package com.app.japub.domain.dto;

import lombok.Data;

@Data
public class ScheduleDto {
    private Long scheduleNum;
    private String scheduleContent;
    private int schedulePrice;
    private String scheduleState;
    private String scheduleReservationDate;
    private String scheduleRegisterDate;
    private String scheduleUpdateDate;
}

