package com.app.japub.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.japub.domain.dto.BoardDto;
import com.app.japub.domain.dto.ProductDto;
import com.app.japub.domain.service.board.BoardService;
import com.app.japub.domain.service.product.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	private final BoardService boardService;
	private final ProductService productService;

	@GetMapping("/")
	public String home() {
		return "forward:/main";
	}

	@GetMapping("/main")
	public void main(Model model) {
		List<ProductDto> recommendProducts = productService.findByProductIsRecommend(true);
		recommendProducts.forEach(productService::setProductThumbnailPath);
		model.addAttribute("recommendProducts", recommendProducts);

		List<ProductDto> newProducts = productService.findByCategoryAndAmount(null, 8);
		newProducts.forEach(productService::setProductThumbnailPath);
		model.addAttribute("newProducts", newProducts);

		List<BoardDto> mediaBoards = boardService.findByCategoryAndAmount("media", 5);
		mediaBoards.forEach(boardService::setBoardRegisterDate);
		model.addAttribute("mediaBoards", mediaBoards);

		List<BoardDto> downloadBoards = boardService.findByCategoryAndAmount("download", 5);
		downloadBoards.forEach(boardService::setBoardRegisterDate);
		model.addAttribute("downloadBoards", downloadBoards);
	}

}
