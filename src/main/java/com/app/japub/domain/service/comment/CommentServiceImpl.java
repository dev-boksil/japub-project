package com.app.japub.domain.service.comment;

import java.util.Collections;
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
		try {
			return commentDao.insert(commentDto) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("commentService insert error");
			return false;
		}
	}

	@Override
	public boolean update(CommentDto commentDto) {
		try {
			return commentDao.update(commentDto) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("commentService update error");
			return false;
		}
	}

	@Override
	public boolean delete(Long userNum, Long commentNum) {
		try {
			return commentDao.delete(userNum, commentNum) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("commentService delete error");
			return false;
		}
	}

	@Override
	public Long countByBoardNum(Long boardNum) {
		try {
			return commentDao.countByBoardNum(boardNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("commentService countByBoardNum error");
			return 0l;
		}
	}

	@Override
	public List<CommentDto> findByCriteriaAndBoardNum(Criteria criteria, Long boardNum) {
		try {
			return commentDao.findByCriteriaAndBoardNum(criteria, boardNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("commentService findByCriteriaAndBoardNum error");
			return Collections.emptyList();
		}
	}

	@Override
	public int getNextPageCount(Criteria criteria, Long boardNum) {
		try {
			criteria.setPage(criteria.getPage() + 1);
			return commentDao.getPageCount(criteria, boardNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("commentService getNextPageCount error");
			return 0;
		}
	}
}
