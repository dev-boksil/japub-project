package com.app.japub.domain.service.comment;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.app.japub.domain.dto.CommentDto;
import com.app.japub.domain.dto.Criteria;

public interface CommentService {
	public abstract boolean insert(CommentDto commentDto);

	public abstract boolean update(CommentDto commentDto);

	public abstract boolean delete(@Param("userNum") Long userNum, @Param("userNum") Long commentNum);

	public abstract Long countByBoardNum(Long boardNum);

	public abstract List<CommentDto> findByCriteriaAndBoardNum(@Param("criteria") Criteria criteria,
			@Param("boardNum") Long boardNum);
	
	public abstract int getNextPageCount(@Param("criteria") Criteria criteria, @Param("boardNum") Long boardNum);
	
	public abstract CommentDto findByCommentNum(Long commentNum);
}
