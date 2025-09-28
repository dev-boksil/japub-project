package com.app.japub.domain.service.board;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.app.japub.domain.dto.BoardDto;
import com.app.japub.domain.dto.Criteria;

public interface BoardService {
	public abstract List<BoardDto> findByCriteria(Criteria criteria);

	public abstract BoardDto findByBoardNum(Long boardNum);

	public abstract void insert(BoardDto BoardDto);

	public abstract void update(BoardDto BoardDto);

	public abstract boolean delete(@Param("userNum") Long userNum, @Param("boardNum") Long boardNum);

	public abstract Long countByCriteria(Criteria criteria);

	public abstract boolean incrementBoardReadCount(Long boardNum);
	
	public abstract void setBoardRegisterDate(BoardDto boardDto);
	
	public abstract void setBoardRegisterDateTime(BoardDto boardDto);
	
	public abstract BoardDto findByUserNumAndBoardNum(@Param("userNum") Long userNum, @Param("boardNum") Long boardNum);
	
	public abstract List<BoardDto> findByCategoryAndAmount(String category, int amount);
	
}
