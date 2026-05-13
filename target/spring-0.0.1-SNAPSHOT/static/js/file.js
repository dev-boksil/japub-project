/*--------------------------------------------------------- 전역변수*/
export const classNames = Object.freeze({ ORIGINAL: "original", NEW: "new", REMOVE: "remove" });
const fileArray = [];
const fileSizeArray = [];
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
			error: xhr => uploadErrorCallback(xhr, length)
		});
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

function refreshFile(index = -1) {
	const dataTransfer = new DataTransfer();

	if (index >= 0) {
		removeFile(index)
		removeFileSize(index)
	}

	fileArray.forEach(file => dataTransfer.items.add(file));
	$("input[name=multipartFiles]")[0].files = dataTransfer.files;
}

function removeFile(index) {
	fileArray.splice(index, 1);
}

function removeFileSize(index) {
	console.log("삭제전사이즈===", fileSizeService.getTotalFileSize());
	fileSizeService.subtractFileSize(fileSizeArray[index]);
	fileSizeArray.splice(index, 1);
	console.log("삭제후사이즈===", fileSizeService.getTotalFileSize());
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
		const downloadFilePath = encodeURIComponent(`${file.fileUploadPath}/${file.fileUuid}_${file.fileName}`);

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

function validateFileSize(files) {
	const maxFileSize = 1024 * 1024 * 700;
	let totalFileSize = 0;

	for (const file of files) {
		totalFileSize += file.size;
	}

	return maxFileSize >= fileSizeService.getTotalFileSize() + totalFileSize;
}

function validateFileName(fileName) {
	let regExp = new RegExp("(.*/)\.(exe|sh|alz)$", "i");
	if (regExp.test(fileName)) {
		return false;
	}
	return true;
}

function uploadErrorCallback(xhr, fileLength) {
	if (xhr.status == 401) {
		alert("로그인 후 사용하실 수 있습니다.");
		location.reload();
		return;
	}

	alert("업로드중 오류가 발생했습니다 잠시 후 다시 시도해 주세요.");

	for (let i = 0; i < fileLength; i++) {
		fileArray.pop();
		const removedSize = fileSizeArray.pop();
		console.log("removedSize==", removedSize);
		fileSizeService.subtractFileSize(removedSize);
	}

	refreshFile();
}

/*---------------------------------------------------------이벤트*/
(function() {
	const isUpdate = $(".container").data("boardUpdate");
	let fileRemoveCount = 0;


	$("input[name=multipartFiles]").on("change", function(e) {
		const files = Array.from(e.target.files);
		const formData = new FormData();
		const boardNum = $(".container").data("boardNum");
		const maxFileCount = 2;
		let fileTotalCount = files.length + fileArray.length + fileRemoveCount;

		console.log("현재 파일사이즈==", fileSizeService.getTotalFileSize());
		if (boardNum) fileService.count(boardNum, count => fileTotalCount += count);

		if (fileTotalCount > maxFileCount) {
			alert("파일은 최대 2개까지만 업로드할 수 있습니다.");
			refreshFile();
			return;
		}

		if (!validateFileSize(files)) {
			alert("업로드 가능한 용량을 초과 하였습니다.");
			refreshFile();
			return;
		}

		for (const file of files) {

			if (!validateFileName(file.name)) {
				alert("업로드 가능한 파일 형식이 아닙니다.");
				refreshFile();
				return;
			}

			if ("download" != category && !isImage(file.type)) {
				alert("이미지 형식만 업로드 가능합니다.");
				refreshFile();
				return;
			}
		}

		for (const file of files) {
			formData.append("multipartFiles", file);
			fileArray.push(file);
			fileSizeArray.push(file.size);
			fileSizeService.addFileSize(file.size);
		}

		fileService.upload(formData, files.length, files => appendThumbnails(files, isUpdate));
		refreshFile();
	});

	$(".thumbnail-ul").on("click", ".file-cancel-btn", function(e) {
		e.preventDefault();
		const $li = $(this).closest("li");
		let index = $("li.new").index($li);

		if (!isUpdate) {
			refreshFile($(".file-cancel-btn").index($(this)));
			$li.remove();
		} else if (index != -1) {
			$li.remove();
			refreshFile(index);
		} else {
			$li.attr("class", classNames.REMOVE).hide();
			--fileRemoveCount;
		}
	});
})();






