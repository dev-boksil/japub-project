package com.app.japub.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.japub.common.SessionUtil;
import com.app.japub.domain.dto.CommentDto;
import com.app.japub.domain.dto.CommentsDto;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.service.admin.AdminService;
import com.app.japub.domain.service.comment.CommentService;
import com.app.japub.domain.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;
	private final AdminService adminService;
	private final UserService userService;
	private final HttpSession session;
	private static final ResponseEntity<String> SUCCESS = ResponseEntity.ok().build();
	private static final ResponseEntity<String> ERROR = new ResponseEntity<String>("작업 중 오류가 발생했습니다 잠시 후 다시 시도해 주세요.",
			HttpStatus.BAD_REQUEST);
	private static final ResponseEntity<String> LOGIN_ERROR = new ResponseEntity<String>("로그인 후 사용하실 수 있습니다.",
			HttpStatus.UNAUTHORIZED);
	private static final ResponseEntity<String> COMMENT_NOT_FOUND = new ResponseEntity<String>("삭제되었거나 존재하지 않는 댓글입니다.",
			HttpStatus.NOT_FOUND);
	private static final ResponseEntity<String> USER_NOT_FOUND = new ResponseEntity<String>("존재하지 않는 회원 입니다.",
			HttpStatus.NOT_FOUND);

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> insert(@RequestBody CommentDto commentDto) {
		System.out.println(commentDto);
		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null || userService.findByUserNum(userNum) == null) {
			session.invalidate();
			return LOGIN_ERROR;
		}

		commentDto.setUserNum(userNum);

		boolean success = commentService.insert(commentDto);

		return success ? SUCCESS : ERROR;
	}

	@PatchMapping(value = "/{commentNum}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> update(@RequestBody CommentDto commentDto, @PathVariable Long commentNum) {

		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null) {
			return LOGIN_ERROR;
		}

		commentDto.setCommentNum(commentNum);
		commentDto.setUserNum(userNum);

		boolean success = SessionUtil.isAdmin(session) ? adminService.updateComment(commentDto)
				: commentService.update(commentDto);

		if (success) {
			return SUCCESS;
		}

		if (!SessionUtil.isAdmin(session) && userService.findByUserNum(userNum) == null) {
			session.invalidate();
			return USER_NOT_FOUND;
		}
		
		return commentService.findByCommentNum(commentNum) == null ? COMMENT_NOT_FOUND : ERROR;
	}

	@DeleteMapping(value = "/{commentNum}", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> delete(@PathVariable Long commentNum) {
		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null) {
			return LOGIN_ERROR;
		}

		boolean success = SessionUtil.isAdmin(session) ? adminService.deleteByCommentNum(commentNum)
				: commentService.delete(userNum, commentNum);

		if (success) {
			return SUCCESS;
		}

		if (!SessionUtil.isAdmin(session) && userService.findByUserNum(userNum) == null) {
			session.invalidate();
			return USER_NOT_FOUND;
		}

		return commentService.findByCommentNum(commentNum) == null ? COMMENT_NOT_FOUND : ERROR;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommentsDto> list(Long boardNum, Criteria criteria) {
		List<CommentDto> comments = commentService.findByCriteriaAndBoardNum(criteria, boardNum);
		int nextPageCount = commentService.getNextPageCount(criteria, boardNum);

		return new ResponseEntity<CommentsDto>(new CommentsDto(comments, nextPageCount), HttpStatus.OK);
	}

}
