package com.app.japub.domain.service.product;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.japub.common.DbConstants;
import com.app.japub.domain.dao.product.ProductDao;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.dto.ProductDto;
import com.app.japub.domain.service.file.FileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductDao productDao;
	private final FileService fileService;
	private static final int THUMBNAIL_SIZE = 400;

	@Override
	public boolean insert(MultipartFile multipartFile, ProductDto productDto, String directoryPath, String datePath) {
		try {
			upload(multipartFile, productDto, directoryPath, datePath);
			return productDao.insert(productDto) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService insert error");
			return false;
		}
	}

	@Override
	public boolean deleteByProductNum(Long productNum) {
		try {
			return productDao.deleteByProductNum(productNum) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService deleteByProductNum error");
			return false;
		}
	}

	@Override
	public boolean update(MultipartFile multipartFile, ProductDto productDto, String directoryPath, String datePath) {
		try {
			upload(multipartFile, productDto, directoryPath, datePath);
			return productDao.update(productDto) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService update error");
			return false;
		}
	}

	@Override
	public List<ProductDto> findByCriteria(Criteria criteria) {
		try {
			return productDao.findByCriteria(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService findByCriteria error");
			return Collections.emptyList();
		}
	}

	@Override
	public Long countByCriteria(Criteria criteria) {
		try {
			return productDao.countByCriteria(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService countByCriteria error");
			return 0l;
		}
	}

	@Override
	public void upload(MultipartFile multipartFile, ProductDto productDto, String directoryPath, String datePath) {
		if (multipartFile == null || multipartFile.isEmpty()) {
			return;
		}
		String productUuid = UUID.randomUUID().toString();
		String originalProductName = multipartFile.getOriginalFilename();
		String productName = productUuid + "_" + originalProductName;
		File uploadPath = fileService.getUploadPath(directoryPath, datePath);
		File file = new File(uploadPath, productName);
		try {
			multipartFile.transferTo(file);
			productDto.setProductUuid(productUuid);
			productDto.setProductName(originalProductName);
			productDto.setProductUploadPath(datePath);
			if (!fileService.isImage(file)) {
				throw new RuntimeException("productService upload no image");
			}
			fileService.createThumbnails(file, new File(uploadPath, "t_" + productName), THUMBNAIL_SIZE);
		} catch (Exception e) {
			throw new RuntimeException("productService upload error", e);
		}
	}

	@Override
	public String getProductThumbnailPath(ProductDto productDto) {
		return productDto.getProductUploadPath() + "/t_" + productDto.getProductUuid() + "_"
				+ productDto.getProductName();
	}

	@Override
	public void setProductThumbnailPath(ProductDto productDto) {
		productDto.setProductThumbnailPath(getProductThumbnailPath(productDto));
	}

	@Override
	public void setProductDiscountPrice(ProductDto productDto) {
		productDto.setProductDiscountPrice((int) (productDto.getProductPrice() * 0.9));
	}

	@Override
	public ProductDto findByProductNum(Long productNum) {
		try {
			return productDao.findByProductNum(productNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService findByProductNum error");
			return null;
		}
	}

	@Override
	public List<ProductDto> findByProductIsRecommend(boolean productIsRecommend) {
		try {
			return productDao.findByProductIsRecommend(productIsRecommend);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService findByProductIsRecommend error");
			return Collections.emptyList();
		}
	}

	@Override
	public boolean updateProductIsRecommend(Long productNum, boolean productIsRecommend) {
		try {
			return productDao.updateProductIsRecommend(productNum, productIsRecommend) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService updateProductIsRecommend error");
			return false;
		}
	}

	@Override
	public List<ProductDto> findByYesterDay() {
		try {
			return productDao.findByYesterDay();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService findByYesterDay error");
			return Collections.emptyList();
		}
	}

	@Override
	public void autoDeleteFiles(List<ProductDto> yesterdayProducts, String directoryPath, String yesterdayPath) {
		List<Path> paths = new ArrayList<>();
		yesterdayProducts.stream()
				.map(product -> Paths.get(directoryPath, getProductThumbnailPath(product).replace("t_", "")))
				.forEach(paths::add);
		yesterdayProducts.stream().map(product -> Paths.get(directoryPath, getProductThumbnailPath(product)))
				.forEach(paths::add);
		File dir = new File(directoryPath, yesterdayPath);
		File[] products = dir.listFiles();
		products = products == null ? new File[0] : products;
		Arrays.stream(products).filter(product -> !paths.contains(product.toPath())).forEach(File::delete);
	}

	@Override
	public List<ProductDto> findByCategoryAndAmount(String category, int amount) {
		Criteria criteria = new Criteria();
		criteria.setAmount(amount);
		criteria.setCategory(category);
		try {
			return findByCriteria(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("productService findByCategoryAndAmount error");
			return Collections.emptyList();
		}
	}

}
