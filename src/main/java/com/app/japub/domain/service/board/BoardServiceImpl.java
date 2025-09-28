package com.app.japub.domain.service.board;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.japub.common.DateUtil;
import com.app.japub.common.DbConstants;
import com.app.japub.domain.dao.board.BoardDao;
import com.app.japub.domain.dto.BoardDto;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.service.file.FileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	private final BoardDao boardDao;
	private final FileService fileService;

	@Override
	public List<BoardDto> findByCriteria(Criteria criteria) {
		try {
			return boardDao.findByCriteria(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("boardService findByCriteria error");
			return Collections.emptyList();
		}
	}

	@Override
	public BoardDto findByBoardNum(Long boardNum) {
		try {
			return boardDao.findByBoardNum(boardNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("boardService findByBoardNum error");
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(BoardDto boardDto) {
		if (boardDao.insert(boardDto) != DbConstants.SUCCESS_CODE) {
			throw new RuntimeException("boardService insert error");
		}
		fileService.insertFiles(boardDto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(BoardDto boardDto) {
		fileService.insertFiles(boardDto);
		fileService.deleteFiles(boardDto);
		if (boardDao.update(boardDto) != DbConstants.SUCCESS_CODE) {
			throw new RuntimeException("boardService update error");
		}
	}

	@Override
	public boolean delete(Long userNum, Long boardNum) {
		try {
			return boardDao.delete(userNum, boardNum) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("boardService delete error");
			return false;
		}
	}

	@Override
	public Long countByCriteria(Criteria criteria) {
		try {
			return boardDao.countByCriteria(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("boardService countByBoardNum error");
			return 0l;
		}
	}

	@Override
	public boolean incrementBoardReadCount(Long boardNum) {
		try {
			return boardDao.incrementBoardReadCount(boardNum) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("boardService incrementBoardReadCount error");
			return false;
		}
	}

	@Override
	public BoardDto findByUserNumAndBoardNum(Long userNum, Long boardNum) {
		try {
			return boardDao.findByUserNumAndBoardNum(userNum, boardNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("boardService findByUserNumAndBoardNum error");
			return null;
		}
	}

	@Override
	public List<BoardDto> findByCategoryAndAmount(String category, int amount) {
		Criteria criteria = new Criteria();
		criteria.setCategory(category);
		criteria.setAmount(amount);
		try {
			return findByCriteria(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("boardService findByCategoryAndAmount error");
			return Collections.emptyList();
		}
	}

	@Override
	public void setBoardRegisterDate(BoardDto boardDto) {
		boardDto.setBoardRegisterDate(DateUtil.formatDate(boardDto.getBoardRegisterDate()));
	}

	@Override
	public void setBoardRegisterDateTime(BoardDto boardDto) {
		boardDto.setBoardRegisterDate(DateUtil.formatDateTime(boardDto.getBoardRegisterDate()));

	}

}
