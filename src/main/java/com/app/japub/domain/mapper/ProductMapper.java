package com.app.japub.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.dto.FileDto;
import com.app.japub.domain.dto.ProductDto;

@Mapper
public interface ProductMapper {
	public abstract int insert(ProductDto productDto);
	public abstract int deleteByProductNum(Long productNum);
	public abstract int update(ProductDto productDto);
	public abstract List<ProductDto> findByCriteria(Criteria criteria);
	public abstract Long countByCriteria(Criteria criteria);
	public abstract ProductDto findByProductNum(Long productNum);
	public abstract List<ProductDto> findByRecommend();
	public abstract int updateProductIsRecommend(@Param("productNum") Long productNum, @Param("productIsRecommend") boolean productIsRecommend);
	public abstract List<ProductDto> findByYesterDay();
}
