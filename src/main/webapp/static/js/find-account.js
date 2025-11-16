(function() {
	const findByUserEmail = (email, $btn) => {
		const $dimmed = $(".dimmed-container");
		$.ajax({
			url: `${contextPath}/find-account`,
			method: 'post',
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify(email),
			beforeSend() { $dimmed.show(); },
			complete() { $dimmed.hide(); $btn.data("click", false); },
			success: msg => alert(msg),
			error: xhr => alert(xhr.responseText)
		});
	}

	$("a.find-btn-right").on("click", function(e) {
		e.preventDefault();
		const $btn = $(this);
		if ($btn.data("click")) { return; }
		const $input = $btn.closest("div.find-container").find("input[name=userEmail]");
		let userEmail = $input.val().trim();
		if (!userEmail) { alert("이메일을 입력해 주세요."); return; }
		if (!validateEmail(userEmail)) { alert("잘못된 이메일 형식입니다."); return; }
		$btn.data("click", true);
		findByUserEmail({ userEmail }, $btn);
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