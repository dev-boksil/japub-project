import { getValidationChecks, getPasswordInputs, emptyCheck, changeCss, setValidationCheck } from './join.js';

(function() {
	const { $passwordInput, $passwordCheckInput } = getPasswordInputs();
	const { $cancelBtn, $changeEditBtn, $changeSaveBtn } = getBtns();
	let password = "";

	$changeEditBtn.on("click", function(e) {
		e.preventDefault();
		$changeEditBtn.hide();
		$changeSaveBtn.show();
		$cancelBtn.show();
		password = $passwordInput.val().trim();
		$passwordCheckInput.attr("type", "password");
		$passwordInput.prop("readonly", false).val("").focus();
	});

	$changeSaveBtn.on("click", function(e) {
		e.preventDefault();
		if (!emptyCheck()) { return; }
		const { userPassword, userPasswordCheck } = getValidationChecks();
		if (!userPassword || !userPasswordCheck) { alert("모든 항목을 정확히 입력해 주세요."); return; }
		$(this).closest("form").submit();
	});

	$cancelBtn.on("click", function(e) {
		e.preventDefault();
		$cancelBtn.hide();
		$changeEditBtn.show();
		$changeSaveBtn.hide();
		$passwordInput.val(password).prop("readonly", true);
		$passwordCheckInput.attr("type", "hidden").val("");
		changeCss($passwordInput, true);
		changeCss($passwordCheckInput, true);
		setValidationCheck($passwordInput, false);
		setValidationCheck($passwordCheckInput, false);
	});

	function getBtns() {
		return {
			$cancelBtn: $(".cancelChangePwBtn"),
			$changeEditBtn: $(".changePwBtn"),
			$changeSaveBtn: $(".changePwOkbtn")
		}
	}
})();















































