package com.app.japub.domain.service.comment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.japub.common.DbConstants;
import com.app.japub.domain.dao.comment.CommentDao;
import com.app.japub.domain.dto.CommentDto;
import com.app.japub.domain.dto.Criteria;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentDao commentDao;

	@Override
	public boolean insert(CommentDto commentDto) {
		return commentDao.insert(commentDto) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public boolean update(CommentDto commentDto) {
		return commentDao.update(commentDto) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public boolean delete(Long userNum, Long commentNum) {
		return commentDao.delete(userNum, commentNum) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public Long countByBoardNum(Long boardNum) {
		return commentDao.countByBoardNum(boardNum);
	}

	@Override
	public List<CommentDto> findByCriteriaAndBoardNum(Criteria criteria, Long boardNum) {
		return commentDao.findByCriteriaAndBoardNum(criteria, boardNum);
	}

	@Override
	public int getNextPageCount(Criteria criteria, Long boardNum) {
		criteria.setPage(criteria.getPage() + 1);
		return commentDao.getPageCount(criteria, boardNum);
	}
}
