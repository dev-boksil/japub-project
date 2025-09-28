package com.app.japub.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.japub.common.DateUtil;
import com.app.japub.domain.dto.FileDto;
import com.app.japub.domain.service.file.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;
	private static final String FILES_DIRECTORY = "C:/upload/files";
	private static final String DOWNLOAD_DIRECTORY = "C:/upload/download";

	@GetMapping("/display")
	public ResponseEntity<byte[]> display(String filePath, String category) {
		File file = new File(getDefaultDirectory(category), filePath);
		if (!file.exists()) {
			return ResponseEntity.notFound().build();
		}
		try {
			String contentType = fileService.getContentType(file);
			HttpHeaders header = new HttpHeaders();
			header.set(HttpHeaders.CONTENT_TYPE, contentType);
			byte[] result = FileCopyUtils.copyToByteArray(file);
			return new ResponseEntity<byte[]>(result, header, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> download(String filePath, String category) {
		File file = new File(getDefaultDirectory(category), filePath);
		Resource resource = new FileSystemResource(file);
		if (!resource.exists()) {
			return ResponseEntity.notFound().build();
		}
		String fileName = resource.getFilename();
		fileName = fileName.substring(fileName.indexOf("_") + 1);
		HttpHeaders header = new HttpHeaders();
		try {
			header.add("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
			return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping(value = "/{boardNum}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FileDto>> getFiles(@PathVariable Long boardNum) {
		List<FileDto> files = fileService.findByBoardNum(boardNum);
		return new ResponseEntity<List<FileDto>>(files, HttpStatus.OK);
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FileDto>> upload(MultipartFile[] multipartFiles, String category) {
		List<FileDto> files = new ArrayList<>();
		if (multipartFiles != null) {
			String directoryPath = getDefaultDirectory(category);
			String datePath = DateUtil.getDatePath();
			for (MultipartFile multipartFile : multipartFiles) {
				try {
					FileDto fileDto = fileService.upload(multipartFile, directoryPath, datePath);
					files.add(fileDto);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		return new ResponseEntity<List<FileDto>>(files, HttpStatus.OK);
	}

	@GetMapping(value = "/count/{boardNum}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getCount(@PathVariable Long boardNum) {
		return new ResponseEntity<Integer>(fileService.countByBoardNum(boardNum), HttpStatus.OK);
	}

	private String getDefaultDirectory(String category) {
		return "download".equals(category) ? DOWNLOAD_DIRECTORY : FILES_DIRECTORY;
	}

}
