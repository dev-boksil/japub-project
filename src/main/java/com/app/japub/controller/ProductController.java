package com.app.japub.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.japub.common.DateUtil;
import com.app.japub.common.MessageConstants;
import com.app.japub.common.SessionUtil;
import com.app.japub.common.ViewPathUtil;
import com.app.japub.domain.dto.Criteria;
import com.app.japub.domain.dto.PageDto;
import com.app.japub.domain.dto.ProductDto;
import com.app.japub.domain.service.file.FileService;
import com.app.japub.domain.service.product.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	private final FileService fileService;
	private final HttpSession session;
	private static final String DEFAULT_DIRECTORY = "C:/upload/products";
	private static final int DEFAULT_AMOUNT = 30;
	private static final String DEFAULT_SORT = "recent";
	private static final int MAX_RECOMMEND_SIZE = 8;

	private static final String BASE_PATH = "products";
	private static final String LIST_PATH = "list";
	private static final String REGISTER_PATH = "register";
	private static final String UPDATE_PATH = "update";
	private static final String PRDOUCT_KEY = "product";

	@GetMapping("/list")
	public void list(Criteria criteria, Model model) {
		setSort(criteria);
		criteria.setAmount(DEFAULT_AMOUNT);
		List<ProductDto> products = productService.findByCriteria(criteria);
		products.forEach(productService::setProductDiscountPrice);
		products.forEach(productService::setProductThumbnailPath);
		model.addAttribute("products", products);
		model.addAttribute("pageDto", new PageDto(criteria, productService.countByCriteria(criteria)));
	}

	@GetMapping("/register")
	public String register(Criteria criteria, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfNotAdmin(attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		return ViewPathUtil.getForwardPath(BASE_PATH, REGISTER_PATH);
	}

	@PostMapping("/register")
	public String register(ProductDto productDto, MultipartFile multipartFile, Criteria criteria,
			RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirect = redirectIfNotAdmin(attributes, criteria);
		if (redirect != null) {
			return redirect; // 관리자 아니면 리다이렉트
		}
		boolean isSuccess = productService.insert(multipartFile, productDto, DEFAULT_DIRECTORY, DateUtil.getDatePath());
		if (!isSuccess) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			addProductToFlash(productDto, attributes);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, REGISTER_PATH);
		}
		return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
	}

	@GetMapping("/update")
	public String update(Criteria criteria, Long productNum, Model model, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfNotAdmin(attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		if (model.getAttribute(PRDOUCT_KEY) != null) {
			return ViewPathUtil.getForwardPath(BASE_PATH, UPDATE_PATH);
		}
		redirectPath = redirectIfProductNumIsNull(productNum, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		ProductDto productDto = productService.findByProductNum(productNum);
		redirectPath = redirectIfProductNotFound(productDto, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		productService.setProductThumbnailPath(productDto);
		model.addAttribute(PRDOUCT_KEY, productDto);
		return ViewPathUtil.getForwardPath(BASE_PATH, UPDATE_PATH);
	}

	@PostMapping("/update")
	public String update(MultipartFile multipartFile, Criteria criteria, ProductDto productDto,
			RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfNotAdmin(attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		ProductDto productToValidate = productService.findByProductNum(productDto.getProductNum());
		redirectPath = redirectIfProductNotFound(productToValidate, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		boolean isSuccess = productService.update(multipartFile, productDto, DEFAULT_DIRECTORY, DateUtil.getDatePath());
		if (!isSuccess) {
			addProductToFlash(productDto, attributes);
			attributes.addAttribute("productNum", productDto.getProductNum());
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, UPDATE_PATH);
		}
		return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
	}

	@GetMapping("/recommend/add")
	public String recommend(Criteria criteria, Long productNum, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfNotAdmin(attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		redirectPath = redirectIfProductNumIsNull(productNum, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		ProductDto productDto = productService.findByProductNum(productNum);
		redirectPath = redirectIfProductNotFound(productDto, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		int recommendSize = productService.findByProductIsRecommend(true).size();
		if (MAX_RECOMMEND_SIZE <= recommendSize) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.MAX_RECOMMEND_MSG);
		} else {
			productService.updateProductIsRecommend(productNum, true);
			MessageConstants.addSuccessMessage(attributes, MessageConstants.SUCCESS_MSG);
		}
		return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
	}

	@PostMapping("/recommend/cancel")
	public String recommendCancel(Criteria criteria, Long productNum, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfNotAdmin(attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		redirectPath = redirectIfProductNumIsNull(productNum, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		ProductDto productDto = productService.findByProductNum(productNum);
		redirectPath = redirectIfProductNotFound(productDto, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		boolean isSuccess = productService.updateProductIsRecommend(productNum, false);
		if (isSuccess) {
			MessageConstants.addSuccessMessage(attributes, MessageConstants.SUCCESS_MSG);
		} else {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
		}
		return ViewPathUtil.REDIRECT_MAIN;
	}

	@GetMapping("/delete")
	public String delete(Criteria criteria, Long productNum, RedirectAttributes attributes) {
		Long userNum = SessionUtil.getSessionNum(session);
		if (userNum == null) {
			return ViewPathUtil.REDIRECT_LOGIN;
		}
		String redirectPath = redirectIfNotAdmin(attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		redirectPath = redirectIfProductNumIsNull(productNum, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		ProductDto productDto = productService.findByProductNum(productNum);
		redirectPath = redirectIfProductNotFound(productDto, attributes, criteria);
		if (redirectPath != null) {
			return redirectPath;
		}
		boolean isSuccess = productService.deleteByProductNum(productNum);
		if (!isSuccess) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
		}
		return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
	}

	private String redirectIfProductNotFound(ProductDto productDto, RedirectAttributes attributes, Criteria criteria) {
		if (productDto == null) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.PRODUCT_NOT_FOUND_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}
		return null;
	}

	private String redirectIfProductNumIsNull(Long productNum, RedirectAttributes attributes, Criteria criteria) {
		if (productNum == null) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ERROR_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}
		return null;
	}

	private String redirectIfNotAdmin(RedirectAttributes attributes, Criteria criteria) {
		if (!SessionUtil.isAdmin(session)) {
			MessageConstants.addErrorMessage(attributes, MessageConstants.ADMIN_NOT_ALLOW_MSG);
			return ViewPathUtil.getRedirectPath(criteria, BASE_PATH, LIST_PATH);
		}
		return null;
	}

	private void setSort(Criteria criteria) {
		String sort = criteria.getSort();
		if (sort == null || sort.isEmpty()) {
			criteria.setSort(DEFAULT_SORT);
		}
	}

	private void addProductToFlash(ProductDto productDto, RedirectAttributes attributes) {
		attributes.addFlashAttribute(PRDOUCT_KEY, productDto);
	}
}
