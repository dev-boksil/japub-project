
let click = false;

function findByUserEmail(userEmail) {
	const $dimmed = $(".dimmed-container");

	$.ajax({
		url: `${contextPath}/find-account`,
		method: 'post',
		contentType: 'application/json;charset=UTF-8',
		data: JSON.stringify({ userEmail }),
		beforeSend() { $dimmed.show(); click = true; },
		complete() { $dimmed.hide(); click = false; },
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

$("a.find-btn-right").on("click", function(e) {
	e.preventDefault();
	if (click) return;
	const userEmail = $(this).closest("div.find-container").find("input[name=userEmail]").val().trim();
	if (!userEmail) { alert("이메일을 입력해 주세요."); return; }
	if (!validateEmail(userEmail)) { alert("잘못된 이메일 형식입니다."); return; }
	findByUserEmail(userEmail);
});

$(".find-cancel-btn").on("click", function(e) {
	e.preventDefault();
	if (click) { return; }
	location.href = $(this).attr("href");
});

$("form[name=find-user-form]").on("submit", function(e) {
	e.preventDefault();
	return;
});

