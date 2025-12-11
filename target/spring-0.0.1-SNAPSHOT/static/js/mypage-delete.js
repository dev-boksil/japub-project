$(".user-delete-btn").on("click", function(e) {
	e.preventDefault();
	if (!confirm("회원 탈퇴 시 모든 정보가 삭제되며 복구가 불가능합니다.\n정말 탈퇴하시겠습니까?")) { return; }
	$(this).closest("form").submit();
});
