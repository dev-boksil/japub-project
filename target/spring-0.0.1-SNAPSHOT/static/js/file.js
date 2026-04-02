/*--------------------------------------------------------- 전역변수*/
export const classNames = Object.freeze({ ORIGINAL: "original", NEW: "new", REMOVE: "remove" });
const fileArray = [];
const category = $(".container").data("boardCategory");

/*--------------------------------------------------------- ajax 통신*/
const fileService = (function() {

	function upload(formData, length, callback) {
		const $dimmedImg = $(".dimmed-container");
		const $fileInput = $("input[name=multipartFiles]");

		$.ajax({
			url: `${contextPath}/files/upload?category=${category}`,
			method: 'post',
			contentType: false,
			processData: false,
			data: formData,
			beforeSend() { $fileInput.prop("disabled", true); $dimmedImg.show(); },
			complete() { $fileInput.prop("disabled", false); $dimmedImg.hide(); },   // ← 성공/실패 모두에서 닫기
			success: callback,
			error: () => {
				alert("업로드 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
				for (let i = 0; i < length; i++) { fileArray.pop(); }
				refreshFile(fileArray);
			}
		})
	}

	function getFiles(boardNum, callback) {
		$.ajax({
			url: `${contextPath}/files/${boardNum}`,
			method: 'get',
			success: callback
		})
	}

	function count(boardNum, callback) {
		$.ajax({
			url: `${contextPath}/files/${boardNum}/count`,
			method: 'get',
			async: false,
			success: callback
		})
	}

	return { upload, getFiles, count };
})();

/*-------------------------------------------------------------함수*/
const fileSizeService = (function() {
	let totalFileSize = 0;

	function addFileSize(fileSize) {
		totalFileSize += fileSize;
	}

	function subtractFileSize(fileSize) {
		totalFileSize -= fileSize;
	}

	function getTotalFileSize() {
		return totalFileSize;
	}

	return { addFileSize, subtractFileSize, getTotalFileSize };
})();

export function createHiddenInputs(className = classNames.ORIGINAL) {
	const name = className == classNames.REMOVE ? "deleteFiles" : "insertFiles";
	let html = "";

	$(`li.${className}`).each((i, li) => {
		html += `<input type="hidden" name="${name}[${i}].fileNum" value="${li.dataset.fileNum}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileUuid" value="${li.dataset.fileUuid}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileUploadPath" value="${li.dataset.fileUploadPath}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileName" value="${li.dataset.fileName}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileType" value="${li.dataset.fileType}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileSize" value="${li.dataset.fileSize}" />`;
	});

	return html;
}

function refreshFile(fileArray, fileSizeArray, index = -1) {
	const dataTransfer = new DataTransfer();

	if (index >= 0) {
		removeFile(fileArray, index)
		removeFileSize(fileSizeArray, index)
	}

	fileArray.forEach(file => dataTransfer.items.add(file));
	$("input[name=multipartFiles]")[0].files = dataTransfer.files;
}

function removeFile(fileArray, index) {
	fileArray.splice(index, 1);
}

function removeFileSize(fileSizeArray, index) {
	fileSizeService.subtractFileSize(fileSizeArray[index]);
	fileSizeArray.splice(index, 1);
}

export function showThumbnails(boardNum, isDownload) {
	fileService.getFiles(boardNum, files => appendThumbnails(files, false, isDownload));
}

function appendThumbnails(files, isUpdate, isDownload) {
	$("ul.thumbnail-ul").append(createThumbnails(files, isUpdate, isDownload));
}

function createThumbnails(files, isUpdate = false, isDownload = false) {
	let html = "";

	files.forEach(file => {
		const displayFilePath = encodeURIComponent(`${file.fileUploadPath}/t_${file.fileUuid}_${file.fileName}`);
		const downloadFilePath = displayFilePath.replace("t_", "");

		html += `<li class="${isUpdate ? classNames.NEW : classNames.ORIGINAL}" data-file-num="${file.fileNum ? file.fileNum : ""}" data-file-uuid="${file.fileUuid}" data-file-upload-path="${file.fileUploadPath}" data-file-name="${file.fileName}" data-file-type="${file.fileType}" data-file-size="${file.fileSize}" >`;
		html += isDownload ? `<a href="${contextPath}/files/download?filePath=${downloadFilePath}&category=${category}">` : ``;
		html += file.fileType ? `<img src="${contextPath}/files/display?filePath=${displayFilePath}&category=${category}" width="100" />` : `<img src="${contextPath}/static/images/file/attach.png" width="100" />`;
		html += isDownload ? `<a>` : ``;
		html += !isDownload ? `<img class="file-cancel-btn" src="${contextPath}/static/images/file/cancel.png" width="25"` : ``;
		html += `</li>`;
	});

	return html;
}

function isImage(fileType) {
	return !!fileType && fileType.startsWith("image/");
}

function validateFileSize(fileSize) {
	const maxFileSize = 1024 * 1024 * 700;

	if (maxFileSize < fileSizeService.getTotalFileSize() + fileSize) {
		return false;
	} else {
		fileSizeService.addFileSize(fileSize);
		return true;
	}
}

function validateFileName(fileName) {
	let regExp = new RegExp("(.*/)\.(exe|sh|alz)$", "i");
	if (regExp.test(fileName)) {
		return false;
	}
	return true;
}

/*---------------------------------------------------------이벤트*/
(function() {
	const fileSizeArray = [];
	const isUpdate = $(".container").data("boardUpdate");
	let fileRemoveCount = 0;


	$("input[name=multipartFiles]").on("change", function(e) {
		const files = Array.from(e.target.files);
		const formData = new FormData();
		const boardNum = $(".container").data("boardNum");
		const maxFileCount = 2;
		let fileTotalCount = 0;

		fileTotalCount = files.length + fileArray.length + fileRemoveCount;

		if (boardNum) fileService.count(boardNum, count => fileTotalCount += count);

		if (fileTotalCount > maxFileCount) {
			alert("파일은 최대 2개까지만 업로드할 수 있습니다.");
			refreshFile(fileArray, fileSizeArray);
			return;
		}

		for (const file of files) {
			if (!validateFileSize(file.size)) {
				alert("업로드 가능한 용량을 초과 하였습니다.");
				refreshFile(fileArray, fileSizeArray);
				return;
			}

			if (!validateFileName(file.name)) {
				alert("업로드 가능한 파일 형식이 아닙니다.");
				refreshFile(fileArray, fileSizeArray);
				return;
			}

			if ("download" != category && !isImage(file.type)) {
				alert("이미지 형식만 업로드 가능합니다.");
				refreshFile(fileArray, fileSizeArray);
				return;
			}

			formData.append("multipartFiles", file);
			fileArray.push(file);
			fileSizeArray.push(file.size);
		}

		fileService.upload(formData, files.length, files => isUpdate ? appendThumbnails(files, true) : appendThumbnails(files, false));
		refreshFile(fileArray, fileSizeArray);
	});

	$(".thumbnail-ul").on("click", ".file-cancel-btn", function(e) {
		e.preventDefault();
		const $li = $(this).closest("li");
		let index = $("li.new").index($li);

		if (!isUpdate) {
			refreshFile(fileArray, fileSizeArray, $(".file-cancel-btn").index($(this)));
			$li.remove();
		} else if (index != -1) {
			$li.remove();
			refreshFile(fileArray, fileSizeArray, index);
		} else {
			$li.attr("class", classNames.REMOVE).hide();
			--fileRemoveCount;
		}
	});
})();






