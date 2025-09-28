(function() {
	const findByUserEmail = (email) => {
		$.ajax({
			url: `${contextPath}/find-account`,
			method: 'post',
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify(email),
			success: msg => alert(msg),
			error: xhr => alert(xhr.responseText)
		});
	}

	$("a.find-btn-right").on("click", function(e) {
		e.preventDefault();
		const $input = $(this).closest("div.find-container").find("input[name=userEmail]");
		let userEmail = $input.val().trim();
		if (!userEmail) { alert("이메일을 입력해 주세요."); return; }
		if (!validateEmail(userEmail)) { alert("잘못된 이메일 형식입니다."); return; }
		findByUserEmail({ userEmail });
		alert("요청이 완료되었습니다.\n잠시만 기다려 주세요.");
	});

	$("form[name=find-user-form]").on("submit", function(e) {
		e.preventDefault();
		return;
	});

})();
function validateEmail(email) { /*이메일정규식*/
	const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
	if (!regex.test(email)) {
		return false;
	}
	return true;
}