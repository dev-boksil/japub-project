(function() {

	$("input[name=multipartFile]").on("change", function() {
		const $thumbnail = $(".thumbnail-preview");
		const $input = $(this);
		const file = $input[0].files[0];
		if (!file) { return; }
		if (!file.type.startsWith("image/")) {
			alert("이미지형식만 업로드 가능합니다.");
			$thumbnail.hide();
			$input.val("");
			return;
		}
		const reader = new FileReader();
		reader.onload = (e) => { $thumbnail.attr("src", e.target.result).show(); }
		reader.readAsDataURL(file);
	});

	$(".product-cancel-btn").on("click", function(e) {
		e.preventDefault();
		if (!confirm("상품 등록을 정말 취소하시겠습니까?\n작성 중인 내용은 저장되지 않습니다.")) { return; }
		location.href = $(this).data("url");
	});

	$(".product-register-btn, .product-update-btn").on("click", function(e) {
		e.preventDefault();
		const $btn = $(this);
		const $form = $btn.closest("form");
		const isUpdate = $btn.data("update");
		if (!validateProductForm($form, isUpdate)) { return; }
		$form.submit();
	});
})();


function validateProductForm($form, isUpdate = false) {
	if (!isUpdate) {
		if (!$("input[name=multipartFile]")[0].files[0]) {
			alert("이미지를 업로드 하세요.");
			return false;
		}
	}
	if (!$form.find("input[name=productTitle]").val().trim()) {
		alert("상품명을 입력하세요.");
		return false;
	}
	if (!$form.find("input[name=productPrice]").val().trim()) {
		alert("가격을 입력하세요.");
		return false;
	}
	if (!$form.find("input[name=productUrl]").val().trim()) {
		alert("url을 입력하세요");
		return false;
	}
	if (!$form.find("select[name=productCategory]").val()) {
		alert("카테고리를 선택하세요");
		return false;
	}
	return true;
}





















































































