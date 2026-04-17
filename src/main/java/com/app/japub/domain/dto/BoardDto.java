package com.app.japub.domain.dto;

import java.util.List;

import lombok.Data;

@Data
public class BoardDto {
	private Long boardNum;
	private String boardTitle;
	private String boardContent;
	private String boardRegisterDate;
	private Long boardReadCount;
	private Long boardCommentCount;
	private Long userNum;
	private String boardUpdateDate;
	private String boardCategory;
	private String boardVideoId;
	private String boardLinkUrl;
	private String userId;
	private List<FileDto> files;
	private List<FileDto> insertFiles;
	private List<FileDto> deleteFiles;
}
