package com.app.japub.controller;

import java.io.File;
import java.io.IOException;
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
	public ResponseEntity<byte[]> display(String filePath, String category) throws IOException {
		File file = new File(getDefaultDirectory(category), filePath);

		if (!file.exists()) {
			return ResponseEntity.notFound().build();
		}

		String contentType = fileService.getContentType(file);
		HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.CONTENT_TYPE, contentType);
		byte[] result = FileCopyUtils.copyToByteArray(file);
		return new ResponseEntity<byte[]>(result, header, HttpStatus.OK);
	}

	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> download(String filePath, String category) throws UnsupportedEncodingException {
		File file = new File(getDefaultDirectory(category), filePath);
		Resource resource = new FileSystemResource(file);

		if (!resource.exists()) {
			return ResponseEntity.notFound().build();
		}

		String fileName = resource.getFilename();
		fileName = fileName.substring(fileName.indexOf("_") + 1);
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION,
				"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FileDto>> getFiles(Long boardNum) {
		return new ResponseEntity<List<FileDto>>(fileService.findByBoardNum(boardNum), HttpStatus.OK);
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FileDto>> upload(MultipartFile[] multipartFiles, String category) {
		List<FileDto> files = new ArrayList<>();

		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				FileDto fileDto = fileService.upload(multipartFile, getDefaultDirectory(category),
						DateUtil.getDatePath());
				files.add(fileDto);
			}
		}
		return new ResponseEntity<List<FileDto>>(files, HttpStatus.OK);
	}

	@GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getCount(Long boardNum) {
		return new ResponseEntity<Integer>(fileService.countByBoardNum(boardNum), HttpStatus.OK);
	}

	private String getDefaultDirectory(String category) {
		return "download".equals(category) ? DOWNLOAD_DIRECTORY : FILES_DIRECTORY;
	}

}
