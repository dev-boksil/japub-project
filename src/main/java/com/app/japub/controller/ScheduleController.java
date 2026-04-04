package com.app.japub.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.japub.common.MessageConstants;
import com.app.japub.common.SessionUtil;
import com.app.japub.common.ViewPathUtil;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.dto.ScheduleDto;
import com.app.japub.domain.dto.SchedulesDto;
import com.app.japub.domain.service.schedule.ScheduleService;
import com.app.japub.enums.RESERVATION;

import lombok.RequiredArgsConstructor;

@RequestMapping("/schedules")
@Controller
@RequiredArgsConstructor
public class ScheduleController {
	private final ScheduleService scheduleService;
	private final HttpSession session;
	private final static ResponseEntity<Void> SUCCESS_CODE = ResponseEntity.ok().build();
	private final static ResponseEntity<Void> ERROR_CODE = ResponseEntity.badRequest().build();
	private final static ResponseEntity<Void> FORBIDDEN = new ResponseEntity<>(HttpStatus.FORBIDDEN);
	private final static ResponseEntity<Void> UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

	@GetMapping("/list")
	public String list(Model model, HttpServletRequest req, Criteria criteria, RedirectAttributes attributes) {
		if (!SessionUtil.isLogin(session)) {
			criteria.setToUri(req.getRequestURI());
			return ViewPathUtil.getRedirectPath(criteria, "login", null);
		}

		if (!SessionUtil.isAdmin(session)) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.PERMISSION_NOT_ALLOW_MSG);
			return ViewPathUtil.REDIRECT_MAIN;
		}

		SessionUtil.addIsAdminToModel(model, session);

		return "schedule/schedule";
	}

	@GetMapping("/modal/{scheduleNum}")
	@ResponseBody
	public ResponseEntity<?> getSchedules(@PathVariable Long scheduleNum) {
		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null) {
			return UNAUTHORIZED;
		}

		if (!SessionUtil.isAdmin(session)) {
			return FORBIDDEN;
		}

		return new ResponseEntity<>(scheduleService.findByScheduleNum(scheduleNum), HttpStatus.OK);
	}

	@GetMapping("/{page}")
	@ResponseBody
	public ResponseEntity<?> getSchedulesDto(@PathVariable int page, String date) {
		Calendar calendar = Calendar.getInstance();

		if (date != null && !date.isBlank()) {
			try {
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
			} catch (ParseException e) {
				return ResponseEntity.badRequest().header(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
						.body("날짜 형식이 올바르지 않습니다.");
			}
		}

		String startDate = scheduleService.getStartDate(calendar, page);
		String endDate = scheduleService.getEndDate(calendar, page);
		List<ScheduleDto> schedules = scheduleService.findByStartDateAndEndDate(startDate, endDate); // 실제db 데이터
		List<String> weekDates = scheduleService.getWeekDates(startDate, endDate); // 현재날짜기준 7일 가져오기
		String today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		return new ResponseEntity<>(new SchedulesDto(schedules, weekDates, today), HttpStatus.OK);
	}

	@PostMapping
	@ResponseBody
	public ResponseEntity<Void> insert(@RequestBody ScheduleDto scheduleDto) {
		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null) {
			return UNAUTHORIZED;
		}

		if (!SessionUtil.isAdmin(session)) {
			return FORBIDDEN;
		}

		String scheduleState = scheduleDto.getScheduleState();

		if (!validateScheduleState(scheduleState)) {
			return ERROR_CODE;
		}

		scheduleDto.setScheduleState(RESERVATION.valueOf(scheduleState).name());

		return scheduleService.insert(scheduleDto) ? SUCCESS_CODE : ERROR_CODE;
	}

	@PatchMapping("/{scheduleNum}")
	@ResponseBody
	public ResponseEntity<Void> update(@RequestBody ScheduleDto scheduleDto) {
		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null) {
			return UNAUTHORIZED;
		}

		if (!SessionUtil.isAdmin(session)) {
			return FORBIDDEN;
		}

		String scheduleState = scheduleDto.getScheduleState();

		if (!validateScheduleState(scheduleState)) {
			return ERROR_CODE;
		}

		scheduleDto.setScheduleState(RESERVATION.valueOf(scheduleState).name());

		boolean success = scheduleService.update(scheduleDto);

		if (success) {
			return SUCCESS_CODE;
		}

		if (scheduleService.findByScheduleNum(scheduleDto.getScheduleNum()) == null) {
			return ResponseEntity.notFound().build();
		}

		return ERROR_CODE;
	}

	@DeleteMapping("/{scheduleNum}")
	@ResponseBody
	public ResponseEntity<Void> delete(@PathVariable Long scheduleNum) {
		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null) {
			return UNAUTHORIZED;
		}

		if (!SessionUtil.isAdmin(session)) {
			return FORBIDDEN;
		}

		boolean success = scheduleService.deleteByScheduleNum(scheduleNum);

		if (success) {
			return SUCCESS_CODE;
		}

		if (scheduleService.findByScheduleNum(scheduleNum) == null) {
			return ResponseEntity.notFound().build();
		}

		return ERROR_CODE;
	}

	private boolean validateScheduleState(String scheduleState) {
		RESERVATION[] reservations = RESERVATION.values();

		return Arrays.stream(reservations).map(Enum::name).anyMatch(name -> name.equals(scheduleState));
	}
}
