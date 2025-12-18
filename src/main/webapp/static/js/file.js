const fileService = (function() {

	function upload(formData, category, callback) {
		const $dimmedImg = $(".dimmed-container");
		const $fileInput = $("input[name=multipartFiles]");

		$.ajax({
			url: `${contextPath}/files/upload?category=${category}`,
			method: 'post',
			contentType: false,
			processData: false,
			data: formData,
			beforeSend() { $dimmedImg.show(); },
			complete() { $fileInput.attr("disabled", false); $dimmedImg.hide(); },   // ← 성공/실패 모두에서 닫기
			success: callback
		})
	}

	function getFiles(boardNum, callback) {
		$.ajax({
			url: `${contextPath}/files?boardNum=${boardNum}`,
			method: 'get',
			success: callback
		})
	}

	function count(boardNum, callback) {
		$.ajax({
			url: `${contextPath}/files/count?boardNum=${boardNum}`,
			method: 'get',
			async: false,
			success: callback
		})
	}

	return { upload, getFiles, count };
})();

export const getClassNames = (function() {
	const classNames = { ORIGINAL: "original", NEW: "new", REMOVE: "remove" };

	return function() {
		return Object.assign({}, classNames);
	}
})();

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

export function createHiddenInputs(className) {
	const { REMOVE } = getClassNames();
	const name = className === REMOVE ? "deleteFiles" : "insertFiles";
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
	const thumbnails = createThumbnails(files, isUpdate, isDownload);
	$("ul.thumbnail-ul").append(thumbnails);
}

function getCategory() {
	return $(".container").data("boardCategory");
}

function createThumbnails(files, isUpdate = false, isDownload = false) {
	const category = getCategory();
	let html = "";
	files.forEach(file => {
		const { ORIGINAL, NEW } = getClassNames();
		const className = isUpdate ? NEW : ORIGINAL;
		const fileNum = file.fileNum ? file.fileNum : "";
		let displayFilePath = encodeURIComponent(`${file.fileUploadPath}/t_${file.fileUuid}_${file.fileName}`);
		let downloadFilePath = displayFilePath.replace("t_", "");

		html += `<li class="${className}" data-file-num="${fileNum}" data-file-uuid="${file.fileUuid}" data-file-upload-path="${file.fileUploadPath}" data-file-name="${file.fileName}" data-file-type="${file.fileType}" data-file-size="${file.fileSize}" >`;
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
	if (maxFileSize > fileSizeService.getTotalFileSize() + fileSize) {
		fileSizeService.addFileSize(fileSize);
		return true;
	}
	return false;
}

function validateFileName(fileName) {
	let regExp = new RegExp("(.*/)\.(exe|sh|alz)$", "i");
	if (regExp.test(fileName)) {
		return false;
	}
	return true;
}

(function() {
	const fileArray = [];
	const fileSizeArray = [];
	const isUpdate = $(".container").data("boardUpdate");
	let fileRemoveCount = 0;


	$("input[name=multipartFiles]").on("change", function() {
		const formData = new FormData();
		const boardNum = $(".container").data("boardNum");
		const $input = $(this);
		const files = Array.from($input[0].files);
		const maxFileCount = 2;
		const category = getCategory();
		let fileTotalCount = 0;

		fileTotalCount += fileArray.length + files.length + fileRemoveCount;

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
			$input.attr("disabled", true);
			fileArray.push(file);
			fileSizeArray.push(file.size);
			formData.append("multipartFiles", file);
		}

		fileService.upload(formData, category, files => isUpdate ? appendThumbnails(files, true) : appendThumbnails(files));
		refreshFile(fileArray, fileSizeArray);
	});

	$(".thumbnail-ul").on("click", ".file-cancel-btn", function(e) {
		e.preventDefault();
		const $fileCancelBtn = $(this);
		const $li = $fileCancelBtn.closest("li");
		let index;

		if (!isUpdate) {
			index = $(".file-cancel-btn").index($fileCancelBtn);
			$li.remove();
			refreshFile(fileArray, fileSizeArray, index);
			return;
		}

		index = $("li.new").index($li);

		if (index != -1) {
			$li.remove();
			refreshFile(fileArray, fileSizeArray, index);
		} else {
			$li.attr("class", getClassNames().REMOVE).hide();
			--fileRemoveCount;
		}
	});
})();






