package com.app.japub.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
	private static final String BOARD_NUM_KEY = "boardNum";

	@GetMapping("/list")
	public void list(Criteria criteria, Model model) {
		setCategory(criteria, model);
		List<BoardDto> boards = boardService.findByCriteria(criteria);
		boards.forEach(boardService::setBoardRegisterDateTime);
		model.addAttribute("boards", boards);
		model.addAttribute("pageDto", new PageDto(criteria, boardService.countByCriteria(criteria)));
		model.addAttribute("writable", isPublicCategory(criteria.getCategory()) || SessionUtil.isAdmin(session));
	}

	@GetMapping("/write")
	public String write(Criteria criteria, RedirectAttributes attributes, Model model) {
		Long userNum = SessionUtil.getSessionNum(session);

		String redirectPath = redirectIfNotLoginOrUserNotFound(userNum, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		redirectPath = redirectIfInvalidCategory(criteria, SessionUtil.isAdmin(session), attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		addUserIdToModel(userNum, model);
		return ViewPathUtil.getForwardPath(BASE_PATH, WRITE_PATH);
	}

	@PostMapping("/write")
	public String write(Criteria criteria, BoardDto boardDto, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);

		String redirectPath = redirectIfNotLoginOrUserNotFound(userNum, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		redirectPath = redirectIfInvalidCategory(criteria, SessionUtil.isAdmin(session), attributes);

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

		setBoardDisplayData(boardDto);
		model.addAttribute(BOARD_KEY, boardDto);

		addUserIdToModel(SessionUtil.getSessionNum(session), model);
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

		String redirectPath = redirectIfNotLoginOrUserNotFound(userNum, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		if (model.getAttribute(BOARD_KEY) != null) {
			return ViewPathUtil.getForwardPath(BASE_PATH, UPDATE_PATH);
		}

		redirectPath = redirectIfBoardNumIsNull(boardNum, criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		BoardDto boardDto = boardService.findByBoardNum(boardNum);

		redirectPath = redirectIfBoardNotFound(boardDto, criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		redirectPath = redirectIfNotBoardOwner(userNum, boardDto, criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		setBoardDisplayData(boardDto);
		model.addAttribute(BOARD_KEY, boardDto);
		return ViewPathUtil.getForwardPath(BASE_PATH, UPDATE_PATH);
	}

	@PostMapping("/update")
	public String update(Criteria criteria, BoardDto boardDto, RedirectAttributes attributes, Model model) {
		Long userNum = SessionUtil.getSessionNum(session);
		Long boardNum = boardDto.getBoardNum();

		String redirectPath = redirectIfNotLoginOrUserNotFound(userNum, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		redirectPath = redirectIfBoardNumIsNull(boardNum, criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		boardDto.setUserNum(userNum);

		try {

			boardService.update(boardDto);
			attributes.addAttribute(BOARD_NUM_KEY, boardNum);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, DETAIL_PATH);
		} catch (Exception e) {
			e.printStackTrace();

			BoardDto dbBoard = boardService.findByBoardNum(boardNum);

			redirectPath = redirectIfBoardNotFound(dbBoard, criteria, attributes);

			if (redirectPath != null) {
				return redirectPath;
			}

			redirectPath = redirectIfNotBoardOwner(userNum, dbBoard, criteria, attributes);

			if (redirectPath != null) {
				return redirectPath;
			}

			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			attributes.addFlashAttribute(BOARD_KEY, boardDto);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, UPDATE_PATH);
		}
	}

	@GetMapping("/delete")
	public String delete(Criteria criteria, Long boardNum, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);

		String redirectPath = redirectIfNotLoginOrUserNotFound(userNum, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		redirectPath = redirectIfBoardNumIsNull(boardNum, criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		boolean isAdmin = SessionUtil.isAdmin(session);
		boolean isDeleted = isAdmin ? adminService.deleteByBoardNum(boardNum) : boardService.delete(userNum, boardNum);

		if (isDeleted) {
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}

		BoardDto boardDto = boardService.findByBoardNum(boardNum);

		redirectPath = redirectIfBoardNotFound(boardDto, criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		redirectPath = isAdmin ? null : redirectIfNotBoardOwner(userNum, boardDto, criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
		attributes.addAttribute(BOARD_NUM_KEY, boardNum);
		return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, DETAIL_PATH);
	}

	private String redirectIfNotLoginOrUserNotFound(Long userNum, RedirectAttributes attributes) {
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}

		return redirectIfUserNotFound(userNum, attributes);
	}

	private String redirectIfUserNotFound(Long userNum, RedirectAttributes attributes) {
		UserDto userDto = userService.findByUserNum(userNum);

		if (userDto == null) {
			session.invalidate();
			MessageConstants.addErrorMessage(attributes, MessageConstants.USER_NOT_FOUND_MSG);
			return ViewPathUtil.REDIRECT_LOGIN;
		}

		return null;
	}

	private String redirectIfNotBoardOwner(Long sessionUserNum, BoardDto boardDto, Criteria criteria,
			RedirectAttributes attributes) {
		String redirectPath = redirectIfBoardNumIsNull(boardDto.getBoardNum(), criteria, attributes);

		if (redirectPath != null) {
			return redirectPath;
		}

		if (!Objects.equals(sessionUserNum, boardDto.getUserNum())) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.BOARD_NO_PERMISSION_MSG);
			attributes.addAttribute(BOARD_NUM_KEY, boardDto.getBoardNum());
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
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
			criteria.setCategory(DEFAULT_CATEGORY);
			MessageConstants.addErrorMessage(attributes, MessageConstants.CATEGORY_NOT_FOUND_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}

		if (isAdminCategory(criteria.getCategory()) && !isAdmin) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ADMIN_NOT_ALLOW_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}

		return null;
	}

	private void setCategory(Criteria criteria, Model model) {
		String category = criteria.getCategory();
		if (category == null || category.isBlank()) {
			criteria.setCategory(DEFAULT_CATEGORY);
		}

		if (!isValidCategory(category)) {

			if (!model.containsAttribute(MessageConstants.KEY_MSG)) {
				model.addAttribute(MessageConstants.KEY_MSG, MessageConstants.CATEGORY_NOT_FOUND_MSG);
			}

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

	private void addUserIdToModel(Long userNum, Model model) {
		UserDto userDto = userService.findByUserNum(userNum);
		model.addAttribute("userId", userDto == null ? "" : userDto.getUserId());
	}

	private void setBoardDisplayData(BoardDto boardDto) {
		boardService.setBoardRegisterDate(boardDto);
		boardDto.setBoardCommentCount(commentService.countByBoardNum(boardDto.getBoardNum()));
	}

	private boolean isPublicCategory(String category) {
		return isValidCategory(category) && !isAdminCategory(category);
	}

	private boolean isValidCategory(String category) {
		return CATEGORIES.contains(category);
	}

	private boolean isAdminCategory(String category) {
		return ADMIN_CATEGORIES.contains(category);
	}

}
