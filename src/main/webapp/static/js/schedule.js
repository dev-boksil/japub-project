$(document).ready(function() {
	let page = 0; //0이 1페이지
	showSchedules(page);

	$("table.schedule-table").on("click", "td.slot", function() {
		if (!checkAccess()) return;
		setModalContent($(this));
	});

	$("img.next-btn").on("click", function() {
		showSchedules(++page);
	});

	$("img.prev-btn").on("click", function() {
		showSchedules(--page);
	});

	$(".btn_today").on("click", function() {
		showSchedules(0);
	});

	$(".btn_input_date").on("click", function() {
		const date = prompt("날짜를 입력하세요", "YYYY-MM-DD");

		if (date == null || !date.trim()) {
			alert("날짜를 입력해주세요.");
			return;
		}

		if (date === "YYYY-MM-DD") {
			return;
		}

		showSchedules(0, date);
	});

	$(".btn_close,.btn_close").on("click", closeModal);

	$(".modal-register").on("click", function() {
		const schedule = getModalValues();
		delete schedule['scheduleNum'];
		if (!emptyCheck(schedule, false)) return;
		scheduleService.insert(schedule, () => {
			closeModal();
			showSchedules(page);
		});
	});

	$(".modal-update").on("click", function() {
		if (!confirm("정말로 수정 하시겠습니까?")) return;
		const schedule = getModalValues();
		if (!emptyCheck(schedule, true)) return;
		scheduleService.update(schedule, () => {
			closeModal();
			showSchedules(page);
		});
	});

	$(".btn_delete").on("click", function() {
		if (!confirm("정말로 삭제 하시겠습니까?")) return;
		const scheduleNum = $(this).closest(".custom_modal_overlay").find("input[name=scheduleNum]").val().trim();
		scheduleService.remove(scheduleNum, () => {
			closeModal();
			showSchedules(page);
		});
	});
});

const scheduleService = (function() {
	function getSchedulesDto(page, date, callback) {
		$.ajax({
			url: `${contextPath}/schedules/${page}?date=${date}`,
			method: "get",
			success: callback,
			error: errorCallback
		});
	}

	function findByScheduleNum(scheduleNum, callback) {
		$.ajax({
			url: `${contextPath}/schedules/modal/${scheduleNum}`,
			method: 'get',
			success: callback,
			error: errorCallback
		});
	}

	function insert(schedule, callback) {
		$.ajax({
			url: `${contextPath}/schedules`,
			method: "post",
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify(schedule),
			success: callback,
			error: errorCallback
		});
	}

	function update(schedule, callback) {
		$.ajax({
			url: `${contextPath}/schedules/${schedule.scheduleNum}`,
			method: "patch",
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify(schedule),
			success: callback,
			error: errorCallback
		});
	}

	function remove(scheduleNum, callback) {
		$.ajax({
			url: `${contextPath}/schedules/${scheduleNum}`,
			method: "delete",
			success: callback,
			error: errorCallback
		});
	}
	return { getSchedulesDto, insert, update, findByScheduleNum, remove };
})();


function closeModal() {
	const $modal = $(".custom_modal_overlay");
	$modal.find("input").val("");
	$modal.find("select").val("");
	$(".custom_modal_overlay").removeClass("show");
}

function setModalContent($td) {
	const $modal = $("div.custom_modal_overlay");
	const scheduleNum = $td.data("scheduleNum");

	$("strong.reservationDate").text($td.data("dateRange"));

	if (scheduleNum) {
		scheduleService.findByScheduleNum(scheduleNum, schedule => {
			$modal.find("input[name=scheduleNum]").val(schedule.scheduleNum);
			$modal.find("select[name=scheduleState]").val(schedule.scheduleState);
			$modal.find("input[name=scheduleContent]").val(schedule.scheduleContent);
			$modal.find("input[name=schedulePrice]").val(schedule.schedulePrice);
			$(".modal-register").hide();
			$(".modal-update").css("display", "inline-block");
			$(".btn_delete").css("display", "inline-block");
		});
	} else {
		$("input[name=reservationDate]").val($td.data("currentDate"));
		$(".modal-register").css("display", "inline-block");
		$(".modal-update").hide();
		$(".btn_delete").hide();
	}

	$modal.addClass("show");
}

function getModalValues() {
	const $modal = $(".custom_modal_overlay");
	return {
		scheduleNum: $modal.find("input[name=scheduleNum").val().trim(),
		scheduleState: $modal.find("select[name=scheduleState]").val().trim(),
		schedulePrice: $modal.find("input[name=schedulePrice]").val().trim(),
		scheduleContent: $modal.find("input[name=scheduleContent]").val().trim(),
		scheduleReservationDate: $modal.find("input[name=reservationDate]").val().trim()
	}
}

function emptyCheck({ scheduleNum, scheduleState, schedulePrice, scheduleContent, scheduleReservationDate }, isUpdate = false) {
	if (isUpdate && !scheduleNum) { alert("스케줄번호를 입력하세요."); return false; }
	if (!isUpdate && !scheduleReservationDate) { alert("예약일을 입력하세요"); return false; }
	if (!scheduleState) { alert("상태를 선택하세요"); return false; }
	if ("CLOSED" === scheduleState) return true;
	if (!schedulePrice) { alert("가격을 입력하세요"); return false; }
	if (!scheduleContent) { alert("내용을 입력하세요"); return false; }
	return true;
}

function renderSchedules(schedulesDto) {
	const weekDates = schedulesDto.weekDates;
	const schedules = schedulesDto.schedules;
	const today = schedulesDto.today;
	$("span.date-range").text(`${weekDates[0]} ~ ${weekDates[6]}`);
	$("tr.thead-date").empty().append(createTheadDates(weekDates, today));
	$("tbody.tbody").empty().append(createTbodyRows(weekDates, schedules));
	$("td.slot").each((i, td) => $(td).css("background-color", $(td).data("color")));
}

function showSchedules(page, date = "") {
	scheduleService.getSchedulesDto(page, date, renderSchedules);
}

function createTbodyRows(weekDates, schedules) {
	let html = ``;
	for (let i = 6; i < 24; i++) {
		const hour = String(i).padStart(2, "0");
		const nextHour = String(i + 1).padStart(2, "0");
		html += `<tr>`;
		html += `<th class="time-col">${hour}</th>`;
		for (let j = 0; j < 7; j++) {
			const currentDate = `${weekDates[j]} ${hour}:00`;
			const schedule = schedules.find(schedule => schedule.scheduleReservationDate.trim() === currentDate.trim());
			html += `<td class="slot available" data-schedule-num="${schedule ? schedule.scheduleNum : ''}" data-color="${getColor(schedule ? schedule.scheduleState : "")}" data-current-date="${currentDate}" data-date-range ="${currentDate} ~ ${weekDates[j + 1]} ${nextHour}:00">`;
			html += `<div class="inner">`;
			html += `<span class="slot-time">${hour}:00~${nextHour}:00</span>`;
			html += `<span class="slot-text">${schedule ? schedule.scheduleContent : '예약가능'}</span>`;
			html += `</div>`;
			html += `</td>`;
		}
		html += `</tr>`;
	}
	return html;
}

function getColor(scheduleState) {
	switch (scheduleState) {
		case "RENTAL":
			return "#FFCCC8";
		case "LESSON":
			return "#ffd09e";
		case "EXTERNAL":
			return "#c1ffa2";
		case "CLOSED":
			return "#d8d8d8";
		default:
			return "#c5e6ff";
	}
}


function createTheadDates(weekDates, today) { // th yy.MM.dd 요일 표기
	console.log("today===========", today);
	const dayOfWeek = ["월", "화", "수", "목", "금", "토", "일"];
	let html = `<th class="time-head"></th>`;
	weekDates.forEach((date, i) => {
		if (date.trim() == today.trim()) {
			html += `<th class="thead-th">${formatDate(date)} ${dayOfWeek[i]}<img src="${contextPath}/static/images/schedule/mark_today.png" class="mark_today"></th>`;
		} else {
			html += `<th class="thead-th">${formatDate(date)} ${dayOfWeek[i]}</th>`;
		}
	});
	return html;
}

function formatDate(date) {
	return String(date).substring(5).replace("-", ".");
}

function checkAccess() {
	if (!sessionUserNum) {
		alert("로그인 후 사용하실 수 있습니다.");
		return false;
	}

	if (!isAdmin) {
		alert("해당 작업을 수행할 수 있는 권한이 없습니다.");
		return false;
	}

	return true;
}

function errorCallback(xhr) {
	if (xhr.status == 401) {
		alert("로그인 후 사용하실 수 있습니다.");
		location.reload();
		return;
	}

	if (xhr.status == 401) {
		alert("해당 작업을 수행할 수 있는 권한이 없습니다.");
		location.reload();
		return;
	}

	if (xhr.status == 404) {
		alert("존재하지 않는 스케줄 입니다.");
		location.reload();
		return;
	}

	alert(xhr.responseText ? xhr.responseText : "요청 처리 중 문제가 발생했습니다. 다시 시도해 주세요");
}

