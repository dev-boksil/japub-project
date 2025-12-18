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
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.japub.common.DbConstants;
import com.app.japub.domain.dao.file.FileDao;
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
		return fileDao.findByBoardNum(boardNum);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertFiles(List<FileDto> insertFiles, Long boardNum) {
		if (insertFiles == null || insertFiles.isEmpty()) {
			return;
		}

		for (FileDto insertFile : insertFiles) {
			insertFile.setBoardNum(boardNum);
			if (fileDao.insert(insertFile) != DbConstants.SUCCESS_CODE) {
				throw new RuntimeException("fileService insertFiles error");
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteFiles(List<FileDto> deleteFiles) {
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
			throw new RuntimeException("fileService isImage error");
		}
	}

	@Override
	public String getContentType(File file) {
		String contentType;

		try {
			contentType = Files.probeContentType(file.toPath());
		} catch (IOException e) {
			throw new RuntimeException("fileService getContentType error", e);
		}

		if (contentType == null) {
			String fileName = file.getName().toLowerCase();
			int dot = fileName.lastIndexOf(".");

			if (dot <= 0 || dot == fileName.length() - 1) {
				throw new RuntimeException("fileService getContentType no extension error");
			}

			String extension = fileName.substring(dot + 1);

			switch (extension) {
			case "jpg":
			case "jpeg":
				contentType = "image/jpeg";
				break;
			case "png":
				contentType = "image/png";
				break;
			case "gif":
				contentType = "image/gif";
				break;
			default:
				throw new RuntimeException("fileService getContentType no match extension error");
			}
			return contentType;
		}

		if (!contentType.startsWith("image/")) {
			throw new RuntimeException("fileService getContentType no image error");
		}

		return contentType;
	}

	@Override
	public void createThumbnails(File originalFile, File thumbnailFile, int size) {
		try (InputStream in = new FileInputStream(originalFile);
				OutputStream out = new FileOutputStream(thumbnailFile);) {
			Thumbnailator.createThumbnail(in, out, size, size);
		} catch (Exception e) {
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
			throw new RuntimeException("fileService upload error", e);
		}
	}

	@Override
	public void autoDeleteFiles(List<FileDto> yesterDayFiles, String directoryPath, String yesterdayPath) {
		List<Path> paths = new ArrayList<>();
		yesterDayFiles.stream().map(file -> Paths.get(directoryPath, getFilePath(file))).forEach(paths::add);
		yesterDayFiles.stream().map(file -> Paths.get(directoryPath, getFileThumbnailPath(file))).forEach(paths::add);
		File[] files = new File(directoryPath, yesterdayPath).listFiles();
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
		return fileDao.findByYesterDay();
	}

	@Override
	public int countByBoardNum(Long boardNum) {
		return fileDao.countByBoardNum(boardNum);
	}

}
