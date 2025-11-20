(function() {
	const $findBtn = $("a.find-btn-right");

	$findBtn.on("click", function(e) {
		e.preventDefault();
		if ($findBtn.data("click")) { return; }
		const $input = $findBtn.closest("div.find-container").find("input[name=userEmail]");
		const userEmail = $input.val().trim();
		if (!userEmail) { alert("이메일을 입력해 주세요."); return; }
		if (!validateEmail(userEmail)) { alert("잘못된 이메일 형식입니다."); return; }
		$findBtn.data("click", true);
		findByUserEmail(userEmail);
	});

	$("form[name=find-user-form]").on("submit", function(e) {
		e.preventDefault();
		return;
	});

	function findByUserEmail(userEmail) {
		const $dimmed = $(".dimmed-container");

		$.ajax({
			url: `${contextPath}/find-account`,
			method: 'post',
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify({ userEmail }),
			beforeSend() { $dimmed.show(); },
			complete() { $dimmed.hide(); $findBtn.data("click", false); },
			success: msg => alert(msg),
			error: xhr => alert(xhr.responseText)
		});
	}

	function validateEmail(email) { /*이메일정규식*/
		const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
		if (!regex.test(email)) {
			return false;
		}
		return true;
	}
})();



