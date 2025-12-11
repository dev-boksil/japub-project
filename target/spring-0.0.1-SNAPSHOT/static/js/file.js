let fileTotalSize = 0;


const fileService = (function() {

	function upload(formData, category, callback) {
		const $dimmedImg = $(".dimmed-container");
		$.ajax({
			url: `${contextPath}/files/upload?category=${category}`,
			method: 'post',
			contentType: false,
			processData: false,
			data: formData,
			beforeSend() { $dimmedImg.show(); },
			complete() { $dimmedImg.hide(); },   // ← 성공/실패 모두에서 닫기
			success: callback,
		});
	}

	function getFiles(boardNum, callback) {
		$.ajax({
			url: `${contextPath}/files/${boardNum}`,
			method: 'get',
			success: callback
		});
	}

	function getFileCount(boardNum, callback) {
		$.ajax({
			url: `${contextPath}/files/count/${boardNum}`,
			method: 'get',
			async: false,
			success: callback
		});
	}
	return { upload, getFiles, getFileCount }
})();


(function() {
	const fileArray = [];
	const fileSizeArray = [];
	const $fileInput = $("input[name=multipartFiles]");
	const isUpdate = $fileInput.data("update");
	let removeCount = 0;

	$fileInput.on("change", function() {
		const boardNum = $(".container").data("boardNum");
		const files = Array.from($fileInput[0].files);
		const formData = new FormData();
		const maxCount = 2;
		const category = getCategory();
		let totalCount = 0;
		totalCount = totalCount + fileArray.length + files.length + removeCount;
		if (boardNum) { fileService.getFileCount(boardNum, count => totalCount += count); }
		if (totalCount > maxCount) {
			alert("파일은 최대 2개까지만 업로드할 수 있습니다.");
			refreshFile(fileArray, fileSizeArray);
			return;
		}
		for (const file of files) {
			if ("download" != category && !isImage(file.type)) { alert("이미지 형식만 업로드 가능합니다."); return; }
			if (!validateFileName(file.name)) { alert("업로드 가능한 파일 형식이 아닙니다."); return; }
			if (!validateFileSize(file.size)) { alert("업로드 가능한 용량을 초과 하였습니다."); return; }
			fileArray.push(file);
			fileSizeArray.push(file.size);
			formData.append("multipartFiles", file);
		}
		fileService.upload(formData, category, files => isUpdate ? appendThumbnails(files, true) : appendThumbnails(files, false));
		refreshFile(fileArray, fileSizeArray);
	});

	$(".thumbnail-ul").on("click", ".file-cancel-btn", function(e) {
		e.preventDefault();
		const $cancelBtn = $(this);
		const $li = $cancelBtn.closest("li");
		let index;
		if (!isUpdate) {
			index = $(".file-cancel-btn").index($cancelBtn);
			refreshFile(fileArray, fileSizeArray, index);
			$li.remove();
			return;
		}

		index = $("li.new").index($li);

		if (index != -1) {
			refreshFile(fileArray, fileSizeArray, index);
			$li.remove();
		} else {
			$li.attr("class", "remove").hide();
			--removeCount;
		}
	});

})();

function isEmpty(formData) {
	for (const entry of formData.entries()) {
		return false;
	}
	return true;
}

function getCategory() {
	return $(".container").data("boardCategory");
}

function removeFileSize(fileSizeArray, index = -1) {
	if (index >= 0) {
		fileTotalSize -= fileSizeArray[index];
		fileSizeArray.splice(index, 1);
	}
}

function removeFile(fileArray, index = -1) {
	if (index >= 0) {
		fileArray.splice(index, 1);
	}
}

function refreshFile(fileArray, fileSizeArray, index) {
	const dataTransfer = new DataTransfer();
	removeFile(fileArray, index)
	removeFileSize(fileSizeArray, index);
	fileArray.forEach(file => dataTransfer.items.add(file));
	$("input[name=multipartFiles]")[0].files = dataTransfer.files;
}


function getHiddenInputs(className = "original") {
	const name = className == "remove" ? "deleteFiles" : "insertFiles";
	let html = "";
	$(`li.${className}`).each((i, li) => {
		html += `<input type="hidden" name="${name}[${i}].fileNum" value="${li.dataset.fileNum}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileUuid" value="${li.dataset.fileUuid}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileName" value="${li.dataset.fileName}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileUploadPath" value="${li.dataset.fileUploadPath}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileSize" value="${li.dataset.fileSize}" />`;
		html += `<input type="hidden" name="${name}[${i}].fileType" value="${li.dataset.fileType}" />`;
	});
	return html;
}


function showThumbnails(boardNum, isDownload = true) {
	fileService.getFiles(boardNum, files => appendThumbnails(files, false, isDownload));
}


function appendThumbnails(files, isUpdate, isDownload) {
	const thumbnails = getThumbnails(files, isUpdate, isDownload);
	$("ul.thumbnail-ul").append(thumbnails);
}

function getThumbnails(files, isUpdate = false, isDownload = false) {
	const category = getCategory();
	let html = "";
	files.forEach(file => {
		let displayFilePath = encodeURIComponent(`${file.fileUploadPath}/t_${file.fileUuid}_${file.fileName}`);
		let downloadFilePath = displayFilePath.replace("t_", "");
		let fileNum = file.fileNum ? file.fileNum : "";
		let className = isUpdate ? "new" : "original";
		html += `<li class="${className}" data-file-num ="${fileNum}"  data-file-uuid ="${file.fileUuid}" data-file-upload-path ="${file.fileUploadPath}" data-file-name ="${file.fileName}" data-file-size ="${file.fileSize}" data-file-type ="${file.fileType}">`;
		html += isDownload ? `<a href="${contextPath}/files/download?filePath=${downloadFilePath}&category=${category}">` : ``;
		html += file.fileType ? `<img src="${contextPath}/files/display?filePath=${displayFilePath}&category=${category}" width="100" />` : `<img src="${contextPath}/static/images/file/attach.png" width="100" />`;
		html += isDownload ? `</a>` : ``;
		html += !isDownload ? `<img class="file-cancel-btn" src="${contextPath}/static/images/file/cancel.png" width="25" />` : ``;
		html += `</li>`;
	});
	return html;
}

function isImage(fileType) {
	return fileType != null && fileType.startsWith("image/");
}

function validateFileSize(fileSize) {
	const maxFileSize = 1024 * 1024 * 700;
	fileTotalSize += fileSize;
	if (fileTotalSize > maxFileSize) {
		fileTotalSize -= fileSize;
		return false;
	}
	return true;
}

function validateFileName(fileName) {
	let regExp = new RegExp("(.*/)\.(exe|sh|alz)$", "i");
	if (regExp.test(fileName)) {
		return false;
	}
	return true;
}