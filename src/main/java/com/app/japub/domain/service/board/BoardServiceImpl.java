package com.app.japub.domain.service.board;

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
		return boardDao.findByCriteria(criteria);
	}

	@Override
	public BoardDto findByBoardNum(Long boardNum) {
		return boardDao.findByBoardNum(boardNum);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(BoardDto boardDto) {
		boolean isSuccess = boardDao.insert(boardDto) == DbConstants.SUCCESS_CODE;

		if (!isSuccess) {
			throw new RuntimeException("boardService insert error");
		}

		fileService.insertFiles(boardDto.getInsertFiles(), boardDto.getBoardNum());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(BoardDto boardDto) {
		boolean isSuccess = boardDao.update(boardDto) == DbConstants.SUCCESS_CODE;

		if (!isSuccess) {
			throw new RuntimeException("boardService update error");
		}

		fileService.insertFiles(boardDto.getInsertFiles(), boardDto.getBoardNum());
		fileService.deleteFiles(boardDto.getDeleteFiles());
	}

	@Override
	public boolean delete(Long userNum, Long boardNum) {
		return boardDao.delete(userNum, boardNum) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public Long countByCriteria(Criteria criteria) {
		return boardDao.countByCriteria(criteria);
	}

	@Override
	public boolean incrementBoardReadCount(Long boardNum) {
		return boardDao.incrementBoardReadCount(boardNum) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public BoardDto findByUserNumAndBoardNum(Long userNum, Long boardNum) {
		return boardDao.findByUserNumAndBoardNum(userNum, boardNum);
	}

	@Override
	public List<BoardDto> findByCategoryAndAmount(String category, int amount) {
		Criteria criteria = new Criteria();
		criteria.setCategory(category);
		criteria.setAmount(amount);
		return findByCriteria(criteria);
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
