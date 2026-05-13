$(document).ready(function() {
	let page = 0; //0이 1페이지
	let selectedDate = "";
	showSchedules(page);

	$("table.schedule-table").on("click", "td.slot", function() {
		if (!checkAccess()) return;
		setModalContent(this);
	});

	$("img.next-btn").on("click", function() {
		showSchedules(++page, selectedDate);
	});

	$("img.prev-btn").on("click", function() {
		showSchedules(--page, selectedDate);
	});

	$(".btn_today").on("click", function() {
		page = 0;
		selectedDate = "";
		showSchedules(page, selectedDate);
	});

	$(".btn_input_date").on("click", function() {
		const date = prompt("날짜를 입력하세요", "YYYY-MM-DD");

		if (date == null) return;

		if (date === "YYYY-MM-DD" || !date.trim()) {
			alert("날짜를 입력해주세요.");
			return;
		}

		if (!isValidDateString(date)) {
			alert("날짜 형식은 yyyy-MM-dd 로 입력해 주세요.");
			return;
		}

		page = 0;
		selectedDate = date;
		showSchedules(page, selectedDate);
	});


	$(".btn_close,.btn_close").on("click", () => toggleModal(false));

	$(".modal-register").on("click", function() {
		const schedule = getValuesFromModal();
		delete schedule['scheduleNum'];
		if (!emptyCheck(schedule, false)) return;
		scheduleService.insert(schedule, () => {
			toggleModal(false);
			showSchedules(page, selectedDate);
		});
	});

	$(".modal-update").on("click", function() {
		if (!confirm("정말로 수정 하시겠습니까?")) return;
		const schedule = getValuesFromModal();
		if (!emptyCheck(schedule, true)) return;
		scheduleService.update(schedule, () => {
			toggleModal(false);
			showSchedules(page, selectedDate);
		});
	});

	$(".btn_delete").on("click", function() {
		if (!confirm("정말로 삭제 하시겠습니까?")) return;
		const scheduleNum = $(this).closest(".custom_modal_overlay").find("input[name=scheduleNum]").val().trim();
		scheduleService.remove(scheduleNum, () => {
			toggleModal(false);
			showSchedules(page, selectedDate);
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


function toggleModal(show) {
	const $modal = $(".custom_modal_overlay");

	if (show) {
		$modal.addClass("show")
	} else {
		$modal.removeClass("show");
		$modal.find("input").val("");
		$modal.find("select").val("");
	}

}

function setModalContent(td) {

	const $td = $(td);
	const scheduleNum = $td.data("scheduleNum");
	const $registerBtn = $(".modal-register");
	const $updateBtn = $(".modal-update");
	const $removeBtn = $(".btn_delete");
	const { $scheduleNumInput, $reservationDateInput, $schedulePriceInput, $scheduleContentInput, $scheduleStateSelect } = getEleFromModal();

	if (scheduleNum) {
		scheduleService.findByScheduleNum(scheduleNum, schedule => {
			$scheduleNumInput.val(scheduleNum);
			$schedulePriceInput.val(schedule.schedulePrice);
			$scheduleContentInput.val(schedule.scheduleContent);
			$scheduleStateSelect.val(schedule.scheduleState);
			$registerBtn.hide();
			$updateBtn.css("display", "inline-block");
			$removeBtn.css("display", "inline-block");
		});
	} else {
		$reservationDateInput.val($td.data("scheduleDate"));
		$schedulePriceInput.val("");
		$scheduleContentInput.val("");
		$scheduleStateSelect.val("");
		$registerBtn.css("display", "inline-block");
		$updateBtn.hide();
		$removeBtn.hide();
	}

	$("strong.reservationDate").text($td.data("dateRange"));
	toggleModal(true);
}

function getValuesFromModal() {
	const { $scheduleNumInput, $reservationDateInput, $schedulePriceInput, $scheduleContentInput, $scheduleStateSelect } = getEleFromModal();
	return {
		scheduleNum: $scheduleNumInput.val().trim(),
		scheduleReservationDate: $reservationDateInput.val().trim(),
		schedulePrice: $schedulePriceInput.val().trim(),
		scheduleContent: $scheduleContentInput.val().trim(),
		scheduleState: $scheduleStateSelect.val()
	}
}

function getEleFromModal() {
	return {
		$scheduleNumInput: $("input[name=scheduleNum]"),
		$reservationDateInput: $("input[name=reservationDate]"),
		$schedulePriceInput: $("input[name=schedulePrice]"),
		$scheduleContentInput: $("input[name=scheduleContent]"),
		$scheduleStateSelect: $("select[name=scheduleState]")
	}
}

function emptyCheck({ scheduleNum, scheduleState, schedulePrice, /*scheduleContent*/ scheduleReservationDate }, isUpdate = false) {
	if (isUpdate && !scheduleNum) {
		alert("스케줄번호를 입력하세요.");
		return false;
	}

	if (!isUpdate && !scheduleReservationDate) {
		alert("예약날짜가 존재하지 않습니다.");
		return false;
	}

	if (!scheduleState) {
		alert("카테고리를 선택하세요");
		return false;
	}

	if (!schedulePrice) {
		alert("가격을 입력하세요");
		return false;
	}

	return true;
}

function renderSchedules(schedulesDto) {
	const { schedules, weekDates, today } = schedulesDto;
	$("span.date-range").text(`${weekDates[0]} ~ ${weekDates[6]}`);
	$("tr.thead-date").empty().append(createTheadDates(weekDates, today));
	$("tbody.tbody").empty().append(createTbodyRows(weekDates, schedules));
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
			const scheduleDate = `${weekDates[j]} ${hour}:00`;
			const scheduleRange = `${scheduleDate} ~ ${weekDates[j + 1]} ${nextHour}:00`;
			const schedule = schedules.find(schedule => schedule.scheduleReservationDate.trim() === scheduleDate.trim());
			html += `<td class="slot available" style="background:${getColor(schedule ? schedule.scheduleState : '')}" data-schedule-num="${schedule ? schedule.scheduleNum : ''}" data-schedule-date="${scheduleDate}" data-schedule-range="${scheduleRange}">`;
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
	if (sessionUserNum == null || !sessionUserNum) {
		alert("로그인 후 사용하실 수 있습니다.");
		return false;
	}

	if (isAdmin == null || !isAdmin) {
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

	if (xhr.status == 403) {
		alert("해당 작업을 수행할 수 있는 권한이 없습니다.");
		location.reload();
		return;
	}

	if (xhr.status == 404) {
		alert("존재하지 않는 스케줄 입니다.");
		location.reload();
		return;
	}

	alert("요청 처리 중 문제가 발생했습니다. 다시 시도해 주세요");
}


function isValidDateString(dateStr) {
	if (!/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
		return false;
	}

	const [year, month, day] = dateStr.split("-").map(Number);
	const date = new Date(year, month - 1, day);

	return date.getFullYear() === year
		&& date.getMonth() === month - 1
		&& date.getDate() === day;
}

