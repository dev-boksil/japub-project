(function() {
	const $productForm = $("form[name=productForm]");

	$("a.page").on("click", function(e) {
		e.preventDefault();
		const page = $(this).attr("href");
		$productForm.find("input[name=page]").val(page);
		$productForm.submit();
	});

	$(".sort-key > a").on("click", function(e) {
		e.preventDefault();
		const sort = $(this).attr("href");
		$productForm.find("input[name=sort]").val(sort);
		$productForm.find("input[name=page]").val(1);
		$productForm.submit();
	});

	$(".product-delete-btn").on("click", function(e) {
		e.preventDefault();
		if (!confirm("정말로 삭제하시겠습니까?")) { return; }
		location.href = $(this).attr("href");
	});

	$("a.product-recommend-btn").on("click", function(e) {
		e.preventDefault();
		const $btn = $(this);
		const $li = $btn.closest("li");
		if ($btn.prop("disabled")) { return; }
		if (!confirm("이 상품을 추천 도서로 지정하시겠습니까?")) { return; }
		if ($li.data("isRecommend")) { alert("이미 추천 도서로 지정된 상품입니다."); return; }
		$btn.prop("disabled", true);
		location.href = $btn.attr("href");
	});

	$(".product-search-btn").on("click", function(e) {
		e.preventDefault();
		const $searchBtn = $(this);
		const $form = $searchBtn.closest("form");
		const keyword = $form.find("input[name=keyword]").val().trim();
		if (!keyword) { alert("검색어를 입력하세요"); return; }
		$form.submit();
	});
	
})();
