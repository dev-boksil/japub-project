package com.app.japub.domain.dto;

import lombok.Data;

@Data
public class ProductDto {
	private Long productNum;
	private String productUuid;
	private String productName;
	private String productUploadPath;
	private String productTitle;
	private int productPrice;
	private int productDiscountPrice;
	private String productUrl;
	private String productCategory;
	private String productRegisterDate;
	private String productUpdateDate;
	private String productThumbnailPath;
	private boolean productIsRecommend;
}
