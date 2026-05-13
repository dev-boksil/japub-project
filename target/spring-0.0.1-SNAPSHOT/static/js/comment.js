const commentService = (function() {
	function insert({ boardNum, commentContent, commentParentNum, commentParentId }, callback) {
		$.ajax({
			url: `${contextPath}/comments`,
			method: 'post',
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify({ boardNum, commentContent, commentParentNum, commentParentId }),
			success: callback,
			error: errorCallback
		});
	}

	function update(commentNum, commentContent, callback) {
		$.ajax({
			url: `${contextPath}/comments/${commentNum}`,
			method: 'patch',
			contentType: 'application/json;charset=UTF-8',
			data: JSON.stringify({ commentContent }),
			success: callback,
			error: errorCallback
		});
	}

	function remove(commentNum, callback) {
		$.ajax({
			url: `${contextPath}/comments/${commentNum}`,
			method: 'delete',
			success: callback,
			error: errorCallback
		});
	}

	function getCommentsDto(boardNum, page, callback) {
		$.ajax({
			url: `${contextPath}/comments?boardNum=${boardNum}&page=${page}`,
			method: 'get',
			success: callback,
			error: errorCallback
		});
	}

	return { insert, update, remove, getCommentsDto };
})();


function errorCallback(xhr) {
	const msg = xhr.responseText ? xhr.responseText : "작업 중 오류가 발생했습니다 잠시 후 다시 시도해 주세요.";
	alert(msg);
	location.reload();
	return;
}

function resetUpdateLi($updateLi) {
	const { $updateDoneBtn, $replyDoneBtn, $updateLiTextarea } = getEleToUpdateLi($updateLi);
	$updateDoneBtn.css("display", "inline-block");
	$replyDoneBtn.hide();
	$updateLiTextarea.val("");
	$updateLi.prev().show();
	$updateLi.hide().appendTo($("body"));
}

function getEleToUpdateLi($updateLi) {
	return {
		$cancelBtn: $updateLi.find(".comment-cancel-btn"),
		$updateDoneBtn: $updateLi.find(".comment-update-done"),
		$replyDoneBtn: $updateLi.find(".comment-reply-done"),
		$updateLiTextarea: $updateLi.find("textarea")
	}
}

function showComments(boardNum, page) {
	commentService.getCommentsDto(boardNum, page, appendComments);
}

function appendComments(conmentsDto) {
	$(".comment-ul").empty().append(createComments(conmentsDto));
}

function createComments(commentsDto) {
	const comments = commentsDto.comments;
	const nextCountPage = commentsDto.nextCountPage;
	let html = "";
	comments.forEach(comment => {
		html += `${comment.commentNum != comment.commentParentNum ? '⤷' : ''}`;
		html += `<li data-comment-num="${comment.commentNum}" data-parent-num="${comment.commentParentNum}" data-user-id="${comment.userId}" data-user-num="${comment.userNum}">`;
		html += `<span class="profile">`;
		html += `<i class="fa fa-user-circle" aria-hidden="true"></i>`;
		html += `</span>`;
		html += `<section>`;
		html += `<div>`;
		html += `<span class="writer">${comment.userId}</span>`;
		html += `</div>`;
		html += `<div>`;
		html += `<span class="comment-parent-id">${comment.commentParentId == null ? '' : '@' + comment.commentParentId + ' '}</span>`;
		html += `<span class="comment-content">${comment.commentContent}</span>`;
		html += `</div>`;
		html += `<div class="comment-footer">`;
		html += `<span>${elapsetTime(comment.commentRegisterDate)}</span>`;
		html += createCommentBtns(comment, sessionUserNum, isAdmin);
		html += `</div>`;
		html += `</section>`;
		html += `</li>`;
	});
	if (nextCountPage > 0) {
		html += `<a href="#" class="comment-more-btn" >댓글 더보기</a>`;
	}
	return html;
}

function createCommentBtns(comment, sessionUserNum, isAdmin) {
	if (!isLogin()) return "";

	const isMyComment = comment.userNum == sessionUserNum;
	const canReply = !isMyComment && (comment.commentParentNum == comment.commentNum);
	let html = "";

	if (isAdmin || isMyComment) {
		html += `<a href="#" class="comment-update-btn">수정</a>`;
		html += `<a href="#" class="comment-remove-btn">삭제</a>`;
	}

	if (canReply) {
		html += `<a href="#" class="comment-reply-btn">답글</a>`;
	}

	return html;
}

function isLogin() {
	return sessionUserNum != null && !!sessionUserNum;
}

function elapsetTime(registerDate) {
	const now = new Date();
	const date = new Date(registerDate);
	const gap = (now - date) / 1000;
	const times = [
		{ name: "년", milliseconds: 60 * 60 * 24 * 365 },
		{ name: "개월", milliseconds: 60 * 60 * 24 * 30 },
		{ name: "일", milliseconds: 60 * 60 * 24 },
		{ name: "시간", milliseconds: 60 * 60 },
		{ name: "분", milliseconds: 60 }
	]
	for (const time of times) {
		const result = Math.floor(gap / time.milliseconds);
		if (result > 0) {
			return `${result}${time.name}전`;
		}
	}
	return `방금전`;
}

(function() {
	const boardNum = $(".container").data("boardNum");
	const $updateLi = $(".update-li");
	const $commentUl = $("ul.comment-ul");
	const { $cancelBtn, $updateDoneBtn, $replyDoneBtn, $updateLiTextarea } = getEleToUpdateLi($updateLi);
	let click = false;
	let page = 1;
	showComments(boardNum, page);

	$(".comment-insert-btn").on("click", function(e) { //등록
		e.preventDefault();
		if (!isLogin(sessionUserNum)) { alert("로그인 후 사용하실 수 있습니다."); $insertTextarea.val(""); return; }
		if (click) return;
		const $insertTextarea = $(this).closest(".comment-container").find("textarea")
		const commentContent = $insertTextarea.val().trim();
		if (!commentContent) { alert("댓글을 입력해주세요"); return; }
		commentService.insert({ boardNum, commentContent }, () => {
			$insertTextarea.val("");
			showComments(boardNum, page);
		});
	});

	$cancelBtn.on("click", function(e) { //취소
		e.preventDefault();
		resetUpdateLi($updateLi);
		click = false;
	});

	$commentUl.on("click", ".comment-remove-btn", function(e) { //삭제
		e.preventDefault();
		if (click) return;
		if (!confirm("정말로 삭제하시겠습니까?")) { return; }
		click = true;
		const commentNum = $(this).closest("li").data("commentNum");
		commentService.remove(commentNum, () => {
			click = false;
			showComments(boardNum, page)
		});
	});

	$commentUl.on("click", ".comment-update-btn", function(e) { //수정
		e.preventDefault();
		if (click) { return; }
		const $li = $(this).closest("li");
		$updateLi.data("commentNum", $li.data("commentNum"))
		$updateLiTextarea.val($li.find("span.comment-content").text());
		$li.hide().after($updateLi);
		$updateLi.show();
		click = true;
	});

	$commentUl.on("click", ".comment-reply-btn", function(e) { //답글수정
		e.preventDefault();
		if (click) { return; }
		const $li = $(this).closest("li");
		$updateLi.data("parentNum", $li.data("commentNum")).data("parentId", $li.data("userId"));
		$replyDoneBtn.css("display", "inline-block");
		$updateDoneBtn.hide();
		$li.after($updateLi);
		$updateLi.show();
		click = true;
	});

	$updateDoneBtn.on("click", function(e) {
		e.preventDefault();
		const commentContent = $updateLiTextarea.val().trim();
		if (!commentContent) { alert("댓글을 입력하세요."); return; }
		const commentNum = $updateLi.data("commentNum");
		commentService.update(commentNum, commentContent, () => {
			resetUpdateLi($updateLi);
			click = false;
			showComments(boardNum, page);
		});
	});

	$replyDoneBtn.on("click", function(e) {
		e.preventDefault();
		const commentContent = $updateLiTextarea.val().trim();
		if (!commentContent) { alert("댓글을 입력하세요."); return; }
		const commentParentNum = $updateLi.data("parentNum");
		const commentParentId = $updateLi.data("parentId");
		console.log(commentContent);
		commentService.insert({ boardNum, commentContent, commentParentNum, commentParentId }, () => {
			resetUpdateLi($updateLi);
			click = false;
			showComments(boardNum, page);
		});
	});

	$commentUl.on("click", ".comment-more-btn", function(e) {
		e.preventDefault();
		showComments(boardNum, ++page);
	});

})();






