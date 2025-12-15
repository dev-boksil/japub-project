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
	private static final ResponseEntity<Void> SUCCESS_CODE = ResponseEntity.ok().build();
	private static final ResponseEntity<Void> ERROR_CODE = ResponseEntity.badRequest().build();

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> insert(@RequestBody CommentDto commentDto, Long boardNum) {
		Long userNum = (Long) session.getAttribute(SessionUtil.KEY);

		if (userNum == null) {
			return ERROR_CODE;
		}

		if (userService.findByUserNum(userNum) == null) {
			return ResponseEntity.notFound().build();
		}

		commentDto.setUserNum(userNum);
		commentDto.setBoardNum(boardNum);

		return commentService.insert(commentDto) ? SUCCESS_CODE : ERROR_CODE;
	}

	@PatchMapping(value = "/{commentNum}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> update(@RequestBody CommentDto commentDto, @PathVariable Long commentNum) {

		Long userNum = (Long) session.getAttribute(SessionUtil.KEY);

		if (userNum == null) {
			return ERROR_CODE;
		}

		if (userService.findByUserNum(userNum) == null) {
			return ResponseEntity.notFound().build();
		}

		commentDto.setUserNum(userNum);
		commentDto.setCommentNum(commentNum);

		boolean isUpdated = SessionUtil.isAdmin(session) ? adminService.updateComment(commentDto)
				: commentService.update(commentDto);

		return isUpdated ? SUCCESS_CODE : ERROR_CODE;
	}

	@DeleteMapping(value = "/{commentNum}")
	public ResponseEntity<Void> delete(@PathVariable Long commentNum) {
		Long userNum = SessionUtil.getSessionNum(session);

		if (userNum == null) {
			return ERROR_CODE;
		}

		if (userService.findByUserNum(userNum) == null) {
			return ResponseEntity.notFound().build();
		}

		boolean isDeleted = SessionUtil.isAdmin(session) ? adminService.deleteByCommentNum(commentNum)
				: commentService.delete(userNum, commentNum);

		return isDeleted ? SUCCESS_CODE : ERROR_CODE;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommentsDto> list(Long boardNum, Criteria criteria) {
		List<CommentDto> comments = commentService.findByCriteriaAndBoardNum(criteria, boardNum);
		int nextPageCount = commentService.getNextPageCount(criteria, boardNum);

		return new ResponseEntity<CommentsDto>(new CommentsDto(comments, nextPageCount), HttpStatus.OK);
	}

}
