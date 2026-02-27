package com.app.japub.domain.dao.product;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.dto.ProductDto;
import com.app.japub.domain.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {
	private final ProductMapper productMapper;

	@Override
	public int insert(ProductDto productDto) {
		return productMapper.insert(productDto);
	}

	@Override
	public int deleteByProductNum(Long productNum) {
		return productMapper.deleteByProductNum(productNum);
	}

	@Override
	public int update(ProductDto productDto) {
		return productMapper.update(productDto);
	}

	@Override
	public List<ProductDto> findByCriteria(Criteria criteria) {
		return productMapper.findByCriteria(criteria);
	}

	@Override
	public Long countByCriteria(Criteria criteria) {
		return productMapper.countByCriteria(criteria);
	}

	@Override
	public ProductDto findByProductNum(Long productNum) {
		return productMapper.findByProductNum(productNum);
	}

	@Override
	public List<ProductDto> findByRecommend() {
		return productMapper.findByRecommend();
	}

	@Override
	public int updateProductIsRecommend(Long productNum, boolean productIsRecommend) {
		return productMapper.updateProductIsRecommend(productNum, productIsRecommend);
	}

	@Override
	public List<ProductDto> findByYesterDay() {
		return productMapper.findByYesterDay();
	}

}
