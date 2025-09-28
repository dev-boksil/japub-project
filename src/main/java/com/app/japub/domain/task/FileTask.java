package com.app.japub.domain.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.japub.common.DateUtil;
import com.app.japub.domain.service.file.FileService;
import com.app.japub.domain.service.product.ProductService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileTask {
	private final FileService fileService;
	private final ProductService ProductService;
	private static final String DOWNLOAD_DIRECTORY = "C:/upload/download";
	private static final String PRODUCTS_DIRECTORY = "C:/upload/products";
	private static final String FILES_DIRECTORY = "C:/upload/files";

	 @Scheduled(cron = "0 0 2 * * *") 
	/* @Scheduled(cron = "0 * * * * ?") */
	public void autoDeleteFiles() {
		System.out.println("cron");
		fileService.autoDeleteFiles(fileService.findByYesterDay(), DOWNLOAD_DIRECTORY, DateUtil.getYesterDayPath());
		fileService.autoDeleteFiles(fileService.findByYesterDay(), FILES_DIRECTORY, DateUtil.getYesterDayPath());
		ProductService.autoDeleteFiles(ProductService.findByYesterDay(), PRODUCTS_DIRECTORY,
				DateUtil.getYesterDayPath());
	}

}
