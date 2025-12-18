import { getClassNames, createHiddenInputs, showThumbnails } from "./file.js";

(function() {
	const $container = $(".container");
	const boardNum = $container.data("boardNum");
	const isShowThumbnails = "download" === $container.data("boardCategory")
	const isDetail = $container.data("boardDetail");
	if (isUpdate()) showThumbnails(boardNum, false);
	if (isShowThumbnails && isDetail) showThumbnails(boardNum, true);
})();


(function() {
	
	$(".write-board-btn, .update-board-btn").on("click", function(e) {
		e.preventDefault();
		const $form = $(this).closest("form");
		if (!validateForm($form)) return;
		const { ORIGINAL, NEW, REMOVE } = getClassNames();
		const html = isUpdate() ? createHiddenInputs(NEW) + createHiddenInputs(REMOVE) : createHiddenInputs(ORIGINAL);
		$form.append(html).submit();
	});

	$("a.deleteBoardBtn").on("click", function(e) {
		e.preventDefault();
		if (!confirm("정말로 삭제 하시겠습니까?")) { return; }
		location.href = $(this).attr("href");
	});

	$("a.page").on("click", function(e) { /*board-list page click*/
		e.preventDefault();
		const $pageForm = $("form[name=pageForm]");
		$pageForm.find("input[name=page]").val($(this).attr("href"));
		$pageForm.submit();
	});

	$("form[name=searchForm").on("submit", function(e) { /*게시판 검색*/
		e.preventDefault();
		if (!$(this).find("select[name=type]").val()) {
			alert("카테고리를 선택하세요");
			return;
		}
		if (!$(this).find("input[name=keyword]").val()) {
			alert("내용을 입력하세요");
			return;
		}
		$(this).find("input[name=page]").val(1);
		this.submit();
	});
})();

function isUpdate() {
	return $(".container").data("boardUpdate");
}

function validateForm($form) {
	if (!$form.find("input[name=boardTitle]").val().trim()) { alert("제목을 입력하세요."); return false; }
	if (!$form.find("textarea").val().trim()) { alert("내용을 입력하세요"); return false; }
	return true;
}











