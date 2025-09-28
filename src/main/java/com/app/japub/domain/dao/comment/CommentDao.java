package com.app.japub.domain.dao.comment;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.app.japub.domain.dto.CommentDto;
import com.app.japub.domain.dto.Criteria;

public interface CommentDao {
	public abstract int insert(CommentDto commentDto);

	public abstract int update(CommentDto commentDto);

	public abstract int delete(@Param("userNum") Long userNum, @Param("userNum") Long commentNum);

	public abstract Long countByBoardNum(Long boardNum);

	public abstract List<CommentDto> findByCriteriaAndBoardNum(@Param("criteria") Criteria criteria,
			@Param("boardNum") Long boardNum);
	
	public abstract int getPageCount(@Param("criteria") Criteria criteria, @Param("boardNum") Long boardNum);
}
