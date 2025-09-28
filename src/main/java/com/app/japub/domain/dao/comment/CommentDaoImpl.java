package com.app.japub.domain.dao.comment;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.japub.domain.dto.CommentDto;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.mapper.CommentMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentDaoImpl implements CommentDao {
	private final CommentMapper commentMapper;

	@Override
	public int insert(CommentDto commentDto) {
		return commentMapper.insert(commentDto);
	}

	@Override
	public int update(CommentDto commentDto) {
		return commentMapper.update(commentDto);
	}

	@Override
	public int delete(Long userNum, Long commentNum) {
		return commentMapper.delete(userNum, commentNum);
	}

	@Override
	public Long countByBoardNum(Long boardNum) {
		return commentMapper.countByBoardNum(boardNum);
	}

	@Override
	public List<CommentDto> findByCriteriaAndBoardNum(Criteria criteria, Long boardNum) {
		return commentMapper.findByCriteriaAndBoardNum(criteria, boardNum);
	}

	@Override
	public int getPageCount(Criteria criteria, Long boardNum) {
		return commentMapper.getPageCount(criteria, boardNum);
	}
}
