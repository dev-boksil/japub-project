package com.app.japub.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.japub.common.MessageConstants;
import com.app.japub.common.SessionUtil;
import com.app.japub.common.ViewPathUtil;
import com.app.japub.domain.dto.BoardDto;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.dto.FileDto;
import com.app.japub.domain.dto.PageDto;
import com.app.japub.domain.dto.UserDto;
import com.app.japub.domain.service.admin.AdminService;
import com.app.japub.domain.service.board.BoardService;
import com.app.japub.domain.service.comment.CommentService;
import com.app.japub.domain.service.file.FileService;
import com.app.japub.domain.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
	private final HttpSession session;
	private final BoardService boardService;
	private final CommentService commentService;
	private final FileService fileService;
	private final AdminService adminService;
	private final UserService userService;
	private static final String DEFAULT_CATEGORY = "free";
	private static final List<String> CATEGORIES = Arrays.asList("free", "notice", "download", "media");
	private static final List<String> ADMIN_CATEGORIES = Arrays.asList("notice", "media", "download");
	private static final String BASE_PATH = "board";
	private static final String LIST_PATH = "list";
	private static final String WRITE_PATH = "write";
	private static final String UPDATE_PATH = "update";
	private static final String DETAIL_PATH = "detail";
	private static final String DOWNLOAD_CATEGORY = "download";
	private static final String BOARD_KEY = "board";

	@GetMapping("/list")
	public void list(Criteria criteria, Model model) {
		setCategory(criteria);
		List<BoardDto> boards = boardService.findByCriteria(criteria);
		boards.forEach(boardService::setBoardRegisterDateTime);
		model.addAttribute("boards", boards);
		model.addAttribute("pageDto", new PageDto(criteria, boardService.countByCriteria(criteria)));
		model.addAttribute("writable", SessionUtil.isAdmin(session)
				|| (isValidCategory(criteria.getCategory()) && !isAdminCategory(criteria.getCategory())));
	}

	@GetMapping("/write")
	public String write(Criteria criteria, RedirectAttributes attributes, Model model) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfInvalidCategory(criteria, SessionUtil.isAdmin(session), attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		addUserIdToModel(userNum, model);
		return ViewPathUtil.getForwardPath(BASE_PATH, WRITE_PATH);
	}

	@PostMapping("/write")
	public String write(Criteria criteria, BoardDto boardDto, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfInvalidCategory(criteria, SessionUtil.isAdmin(session), attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		boardDto.setUserNum(userNum);
		boardDto.setBoardCategory(criteria.getCategory());
		try {
			boardService.insert(boardDto);
			attributes.addAttribute("category", criteria.getCategory());
			return ViewPathUtil.getRedirectPath(null, BASE_PATH, LIST_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			addBoardToFlash(boardDto, attributes);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, WRITE_PATH);
		}

	}

	@GetMapping("/detail")
	public String detail(Criteria criteria, Long boardNum, RedirectAttributes attributes, Model model) {
		String redirectPath = redirectIfBoardNumIsNull(boardNum, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		BoardDto boardDto = boardService.findByBoardNum(boardNum);
		redirectPath = redirectIfBoardNotFound(boardDto, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		boardService.incrementBoardReadCount(boardNum);
		boardDto = boardService.findByBoardNum(boardNum);
		setBoardDisplayData(boardDto);
		Long userNum = SessionUtil.getSessionNum(session);
		addUserIdToModel(userNum, model);
		addBoardToModel(boardDto, model);
		SessionUtil.addIsAdminToModel(model, session);
		if (!DOWNLOAD_CATEGORY.equals(boardDto.getBoardCategory())) {
			model.addAttribute("showImage", true);
			addFilesToModel(boardNum, model);
		}
		return ViewPathUtil.getForwardPath(BASE_PATH, DETAIL_PATH);
	}

	@GetMapping("/update")
	public String update(Criteria criteria, Long boardNum, RedirectAttributes attributes, Model model) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		if (model.getAttribute(BOARD_KEY) != null) {
			return ViewPathUtil.getForwardPath(BASE_PATH, UPDATE_PATH);
		}
		String redirectPath = redirectIfBoardNumIsNull(boardNum, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		BoardDto boardDto = boardService.findByBoardNum(boardNum);
		redirectPath = redirectIfBoardNotFound(boardDto, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		redirectPath = redirectIfBoardNotOwner(boardDto, userNum, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		setBoardDisplayData(boardDto);
		addBoardToModel(boardDto, model);
		return ViewPathUtil.getForwardPath(BASE_PATH, UPDATE_PATH);
	}

	@PostMapping("/update")
	public String update(Criteria criteria, BoardDto boardDto, RedirectAttributes attributes, Model model) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		Long boardNum = boardDto.getBoardNum();
		BoardDto boardToValidate = boardService.findByBoardNum(boardNum);
		String redirectPath = redirectIfBoardNotFound(boardToValidate, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		redirectPath = redirectIfBoardNotOwner(boardToValidate, userNum, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		try {
			boardDto.setUserNum(userNum);
			boardService.update(boardDto);
			addBoardNumToAttribute(boardNum, attributes);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, DETAIL_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			addBoardToFlash(boardDto, attributes);
			addBoardNumToAttribute(boardNum, attributes);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, UPDATE_PATH);
		}
	}

	@GetMapping("/delete")
	public String delete(Criteria criteria, Long boardNum, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfBoardNumIsNull(boardNum, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		BoardDto boardDto = boardService.findByBoardNum(boardNum);
		redirectPath = redirectIfBoardNotFound(boardDto, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		boolean isAdmin = SessionUtil.isAdmin(session);
		redirectPath = isAdmin ? null : redirectIfBoardNotOwner(boardDto, userNum, criteria, attributes);
		if (redirectPath != null) {
			return redirectPath;
		}
		boolean isSuccess = isAdmin ? adminService.deleteByBoardNum(boardNum) : boardService.delete(userNum, boardNum);
		if (!isSuccess) {
			addBoardNumToAttribute(boardNum, attributes);
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, DETAIL_PATH);
		}
		return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
	}

	private String redirectIfBoardNotOwner(BoardDto boardDto, Long userNum, Criteria criteria,
			RedirectAttributes attributes) {
		if (!userNum.equals(boardDto.getUserNum())) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.PERMISSION_NOT_ALLOW_MSG);
			addBoardNumToAttribute(boardDto.getBoardNum(), attributes);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, DETAIL_PATH);
		}
		return null;
	}

	private String redirectIfBoardNotFound(BoardDto boardDto, Criteria criteria, RedirectAttributes attributes) {
		if (boardDto == null) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.BOARD_NOT_FOUND_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}
		return null;
	}

	private String redirectIfBoardNumIsNull(Long boardNum, Criteria criteria, RedirectAttributes attributes) {
		if (boardNum == null) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}
		return null;
	}

	private void addBoardToFlash(BoardDto boardDto, RedirectAttributes attributes) {
		attributes.addFlashAttribute(BOARD_KEY, boardDto);
	}

	private String redirectIfInvalidCategory(Criteria criteria, boolean isAdmin, RedirectAttributes attributes) {
		if (!isValidCategory(criteria.getCategory())) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.CATEGORY_NOT_FOUND_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}
		if (isAdminCategory(criteria.getCategory()) && !isAdmin) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ADMIN_NOT_ALLOW_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}
		return null;
	}

	private void setCategory(Criteria criteria) {
		String category = criteria.getCategory();
		if (category == null || category.isEmpty()) {
			criteria.setCategory(DEFAULT_CATEGORY);
		}
	}

	private void addFilesToModel(Long boardNum, Model model) {
		List<FileDto> files = fileService.findByBoardNum(boardNum);
		if (files != null && !files.isEmpty()) {
			files.forEach(fileService::setFilePath);
			model.addAttribute("files", files);
		}
	}

	private void addBoardNumToAttribute(Long boardNum, RedirectAttributes attributes) {
		attributes.addAttribute("boardNum", boardNum);
	}

	private void addUserIdToModel(Long userNum, Model model) {
		UserDto userDto = userService.findByUserNum(userNum);
		model.addAttribute("userId", userDto == null ? "" : userDto.getUserId());
	}

	private boolean isValidCategory(String category) {
		return CATEGORIES.contains(category);
	}

	private boolean isAdminCategory(String category) {
		return ADMIN_CATEGORIES.contains(category);
	}

	private void addBoardToModel(BoardDto boardDto, Model model) {
		model.addAttribute(BOARD_KEY, boardDto);
	}

	private void setBoardDisplayData(BoardDto boardDto) {
		boardService.setBoardRegisterDate(boardDto);
		boardDto.setBoardCommentCount(commentService.countByBoardNum(boardDto.getBoardNum()));
	}

}
