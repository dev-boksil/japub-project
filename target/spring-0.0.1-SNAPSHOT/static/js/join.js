const validationChecks =
{
	userId: false,
	userPassword: false,
	userPasswordCheck: false,
	userPhone: false,
	userEmail: false
}
const joinService = (function() {

	function checkId(id, callback) {
		$.ajax({
			url: `${contextPath}/join/checkId`,
			method: 'post',
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify(id),
			success: callback
		});
	}

	function checkEmail(email, callback) {
		$.ajax({
			url: `${contextPath}/join/checkEmail`,
			method: 'post',
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify(email),
			success: callback
		});
	}

	return { checkId, checkEmail };
})();

function sample6_execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			// 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

			// 각 주소의 노출 규칙에 따라 주소를 조합한다.
			// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
			var addr = ''; // 주소 변수
			var extraAddr = ''; // 참고항목 변수

			//사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
			if (data.userSelectedType === 'R') {
				// 사용자가 도로명 주소를 선택했을 경우
				addr = data.roadAddress;
			} else {
				// 사용자가 지번 주소를 선택했을 경우(J)
				addr = data.jibunAddress;
			}

			// 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
			if (data.userSelectedType === 'R') {
				// 법정동명이 있을 경우 추가한다. (법정리는 제외)
				// 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
				if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
					extraAddr += data.bname;
				}
				// 건물명이 있고, 공동주택일 경우 추가한다.
				if (data.buildingName !== '' && data.apartment === 'Y') {
					extraAddr += extraAddr !== '' ? ', ' + data.buildingName : data.buildingName;
				}
				// 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
				if (extraAddr !== '') {
					extraAddr = ' (' + extraAddr + ')';
				}
			}

			// 우편번호와 주소 정보를 해당 필드에 넣는다.
			document.getElementById('sample6_postcode').value = data.zonecode;
			document.getElementById('sample6_address').value = addr;
			// 커서를 상세주소 필드로 이동한다.
			document.getElementById('sample6_detailAddress').focus();
		},
	}).open();
}

(function() {
	const $passwordInput = $("input[name=userPassword]");
	const $passwordCheckInput = $("input[name=userPasswordCheck]");

	$("input[name=userId]").on("blur", function() {
		const $input = $(this);
		const userId = $input.val().trim();
		if (!userId || $input.prop("readonly")) { return; }
		const isSuccess = validateId(userId);
		const msg = isSuccess ? "" : "잘못된 아이디 형식입니다.";
		changeCss($input, isSuccess, msg);
		setValidationCheck($input, validationChecks, isSuccess);
		if (!isSuccess) { return; }
		joinService.checkId({ userId }, (isSuccess) => {
			const msg = isSuccess ? "" : "중복된 아이디 입니다.";
			changeCss($input, isSuccess, msg);
			setValidationCheck($input, validationChecks, isSuccess);
		});
	});

	$("input[name=userEmail]").on("blur", function(e) {
		const $input = $(this);
		const userEmail = $input.val().trim();
		if (!userEmail || $input.prop("readonly")) { return; }
		const isSuccess = validateEmail(userEmail);
		const msg = isSuccess ? "" : "잘못된 이메일 형식입니다.";
		changeCss($input, isSuccess, msg);
		setValidationCheck($input, validationChecks, isSuccess);
		if (!isSuccess) { return; }
		joinService.checkEmail({ userEmail }, (isSuccess) => {
			const msg = isSuccess ? "" : "중복된 이메일 입니다.";
			changeCss($input, isSuccess, msg);
			setValidationCheck($input, validationChecks, isSuccess);
		});
	});

	$passwordInput.on("blur", function() {
		const password = $passwordInput.val().trim();
		if (!password || $passwordInput.prop("readonly")) { return; }
		const isSuccess = validatePassword(password);
		const msg = isSuccess ? "" : getPasswordErrorMsg(password);
		changeCss($passwordInput, isSuccess, msg);
		setValidationCheck($passwordInput, isSuccess);
		$passwordCheckInput.trigger("blur");
	});

	$passwordCheckInput.on("blur", function() {
		const passwordConfirm = $passwordCheckInput.val().trim();
		const password = $passwordInput.val().trim();
		if (!password || !passwordConfirm) { return; }
		const isSuccess = password === passwordConfirm;
		const msg = isSuccess ? "" : "두 비밀번호가 서로 일치하지 않습니다.";
		changeCss($passwordCheckInput, isSuccess, msg);
		setValidationCheck($passwordCheckInput, isSuccess);
	});

	$("input[name=userPhone]").on("blur", function() {
		const $input = $(this);
		const phone = $input.val().trim();
		if (!phone || $input.prop("readonly")) { return; }
		const isSuccess = validatePhone(phone);
		const msg = isSuccess ? "" : "잘못된 핸드폰번호 입니다.";
		changeCss($input, isSuccess, msg);
		setValidationCheck($input, validationChecks, isSuccess);
	});

	$("input[class=submit]").on("click", function(e) {
		e.preventDefault();
		if (!emptyCheck()) { return; }
		const result = Object.values(validationChecks).includes(false);
		if (result) { alert("모든 필드를 정확히 입력해주세요."); return; }
		$(this).closest("form").submit();
	});
})();

function setValidationCheck($input, isSuccess) {
	const name = $input.attr("name");
	validationChecks[name] = isSuccess;
}

function changeCss($input, isSuccess, msg) {
	const $span = $input.next("span.error-msg");
	let color = isSuccess ? "rgb(240,240,240)" : "red";
	$input.css({ "outline-color": color, "border-color": color });
	isSuccess ? $span.hide() : $span.text(msg).show();
}

function validateId(id) { /*아이디 정규식*/
	const regex = /^[a-z0-9_-]{5,20}$/;
	if (!regex.test(id)) {
		return false;
	}
	return true;
}

function validatePassword(password) {
	const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_\-+=])[A-Za-z\d!@#$%^&*()_\-+=]{8,16}$/;
	return regex.test(password);
}

// 조건별 상세 체크 함수
function getPasswordErrorMsg(password) {
	if (password.length < 8 || password.length > 16) {
		return "비밀번호는 8자 이상 16자 이하로 입력하세요.";
	}
	if (!/[a-zA-Z]/.test(password)) {
		return "영문자를 최소 1자 이상 포함해야 합니다.";
	}
	if (!/\d/.test(password)) {
		return "숫자를 최소 1자 이상 포함해야 합니다.";
	}
	if (!/[!@#$%^&*()_\-+=]/.test(password)) {
		return "특수문자(!@#$%^&*()_-+=)를 최소 1자 이상 포함해야 합니다.";
	}
	return "";
}

function validateEmail(email) { /*이메일정규식*/
	const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
	if (!regex.test(email)) {
		return false;
	}
	return true;
}

function validatePhone(phoneNumber) {  /*핸드폰정규식*/
	const regex = /^(01[0-9]{1})-?([0-9]{3,4})-?([0-9]{4})$/;
	if (!regex.test(phoneNumber)) {
		return false;
	}
	return true;
}

function emptyCheck() { /*join input empty check*/
	if (!$("input[name = userId]").val().trim()) {
		alert("아이디를 입력해주세요.");
		return false;
	}
	if (!$("input[name = userPassword]").val().trim()) {
		alert("비밀번호를 입력해주세요.");
		return false;
	}
	if (!$("input[name = userPasswordCheck]").val().trim()) {
		alert("비밀번호를 한번더 입력해주세요.");
		return false;
	}
	if (!$("input[name = userPhone]").val().trim()) {
		alert("핸드폰번호를 입력해주세요.");
		return false;
	}
	if (!$("input[name = userEmail]").val().trim()) {
		alert("이메일을 입력해주세요.");
		return false;
	}
	return true;
}