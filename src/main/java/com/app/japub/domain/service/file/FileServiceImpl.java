package com.app.japub.domain.service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.japub.common.DbConstants;
import com.app.japub.domain.dao.file.FileDao;
import com.app.japub.domain.dto.BoardDto;
import com.app.japub.domain.dto.FileDto;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	private final FileDao fileDao;
	private static final int THUMBNAIL_SIZE = 100;

	@Override
	public List<FileDto> findByBoardNum(Long boardNum) {
		try {
			return fileDao.findByBoardNum(boardNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("fileService findByBoardNum error");
			return Collections.emptyList();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFiles(BoardDto boardDto) {
		List<FileDto> insertFiles = boardDto.getDeleteFiles();
		if (insertFiles == null || insertFiles.isEmpty()) {
			return;
		}
		for (FileDto insertFile : insertFiles) {
			insertFile.setBoardNum(boardDto.getBoardNum());
			if (fileDao.insert(insertFile) != DbConstants.SUCCESS_CODE) {
				throw new RuntimeException("fileService insertFiles error");
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteFiles(BoardDto boardDto) {
		List<FileDto> deleteFiles = boardDto.getDeleteFiles();
		if (deleteFiles == null || deleteFiles.isEmpty()) {
			return;
		}
		for (FileDto deleteFile : deleteFiles) {
			if (fileDao.deleteByFileNum(deleteFile.getFileNum()) != DbConstants.SUCCESS_CODE) {
				throw new RuntimeException("fileService deleteFiles error");
			}
		}
	}

	@Override
	public File getUploadPath(String parent, String child) {
		File uploadPath = new File(parent, child);
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}
		return uploadPath;
	}

	@Override
	public boolean isImage(File file) {
		try {
			String contentType = Files.probeContentType(file.toPath());
			return contentType != null && contentType.startsWith("image/");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("fileService isImage error");
		}
	}

	@Override
	public String getContentType(File file) {
		try {
			String contentType = Files.probeContentType(file.toPath());

			if (contentType != null && !contentType.startsWith("image/")) {
				throw new RuntimeException("not image Type");
			}

			if (contentType == null) {
				String fileName = file.getName().toLowerCase();
				int dot = fileName.lastIndexOf(".");

				if (dot <= 0 || dot == fileName.length() - 1) {
					throw new RuntimeException("파일 확장자가 존재하지 않습니다.");
				}

				String extension = fileName.substring(dot + 1);

				if (extension.equals("jpg")) {
					contentType = "image/jpeg";
				} else if (extension.equals("jpeg")) {
					contentType = "image/jpeg";
				} else if (extension.equals("png")) {
					contentType = "image/png";
				} else if (extension.equals("gif")) {
					contentType = "image/gif";
				} else {
					throw new RuntimeException("지원하지 않는 파일 형식입니다.");
				}
			}
			return contentType;
		} catch (IOException e) {
			throw new RuntimeException("fileService getContentType error", e);
		}
	}

	@Override
	public void createThumbnails(File originalFile, File thumbnailFile, int size) {
		try (InputStream in = new FileInputStream(originalFile);
				OutputStream out = new FileOutputStream(thumbnailFile);) {
			Thumbnailator.createThumbnail(in, out, size, size);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("fileService createThumbnails error");
		}
	}

	@Override
	public FileDto upload(MultipartFile multipartFile, String directoryPath, String datePath) {
		String fileUuid = UUID.randomUUID().toString();
		String originalFileName = multipartFile.getOriginalFilename();
		String fileName = fileUuid + "_" + originalFileName;
		File uploadPath = getUploadPath(directoryPath, datePath);
		File file = new File(uploadPath, fileName);
		try {
			multipartFile.transferTo(file);
			FileDto fileDto = new FileDto();
			fileDto.setFileUuid(fileUuid);
			fileDto.setFileName(originalFileName);
			fileDto.setFileSize(multipartFile.getSize());
			fileDto.setFileUploadPath(datePath);
			if (isImage(file)) {
				fileDto.setFileType(true);
				createThumbnails(file, new File(uploadPath, "t_" + fileName), THUMBNAIL_SIZE);
			}
			return fileDto;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("fileService upload error", e);
		}
	}

	@Override
	public void autoDeleteFiles(List<FileDto> yesterDayFiles, String directoryPath, String yesterdayPath) {
		List<Path> paths = new ArrayList<>();
		yesterDayFiles.stream().map(file -> Paths.get(directoryPath, getFileThumbnailPath(file).replace("t_", "")))
				.forEach(paths::add);
		yesterDayFiles.stream().map(file -> Paths.get(directoryPath, getFileThumbnailPath(file))).forEach(paths::add);
		File dir = new File(directoryPath, yesterdayPath);
		File[] files = dir.listFiles();
		files = files == null ? new File[0] : files;
		Arrays.stream(files).filter(file -> !paths.contains(file.toPath())).forEach(File::delete);
	}

	@Override
	public String getFilePath(FileDto fileDto) {
		return getFileThumbnailPath(fileDto).replace("t_", "");
	}

	@Override
	public String getFileThumbnailPath(FileDto fileDto) {
		return fileDto.getFileUploadPath() + "/" + "t_" + fileDto.getFileUuid() + "_" + fileDto.getFileName();
	}

	@Override
	public void setFilePath(FileDto fileDto) {
		fileDto.setFilePath(getFilePath(fileDto));
	}

	@Override
	public List<FileDto> findByYesterDay() {
		try {
			return fileDao.findByYesterDay();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("fileService findByYesterDay error");
			return Collections.emptyList();
		}
	}

	@Override
	public int countByBoardNum(Long boardNum) {
		try {
			return fileDao.countByBoardNum(boardNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("fileService countByBoardNum error");
			return 0;
		}

	}

}
