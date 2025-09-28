<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<link rel="icon" href="<c:url value='/static/images/logo/favicon.png' />" />
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap" rel="stylesheet" />
	<link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
	<link rel="stylesheet" href="<c:url value='/static/css/product-register.css' />" />
	<link rel="stylesheet" href="<c:url value='/static/css/product-update.css' />" />
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
	<main class="main">
		<div class="container">
			<h1 class="product-header">상품 등록</h1>
			<form id="productForm" name="productForm" method="post" enctype="multipart/form-data">
			<input type="hidden" name="productUuid" value="${product.productUuid}" />
			<input type="hidden" name="productThumbnailPath" value="${product.productThumbnailPath}" />
			<input type="hidden" name="productNum" value="${product.productNum}" />
				<div>
					<label for="thumbnail">대표이미지</label>
					<input type="file" id="thumbnail" name="multipartFile" accept="image/*" required />
					<img id="thumbnailPreview" class="thumbnail-preview" src="<c:url value='/upload/products/${product.productThumbnailPath}'/>" style="display:block;" alt="미리보기" />
				</div>
	
				<div>
					<label for="title">제목</label>
					<input type="text" id="title" name="productTitle" placeholder="상품명 입력" value="${product.productTitle}" maxlength="30" required />
				</div>
	
				<div>
					<label for="price">가격 (원)</label>
					<input type="number" id="price" name="productPrice" min="0" placeholder="가격 입력" value="${product.productPrice}" required />
				</div>
	
				<div>
					<label for="url">클릭 시 이동 URL</label>
					<input type="url" id="url" name="productUrl" placeholder="https://example.com" value="${product.productUrl}" required />
				</div>
	
				<div>
					<label for="category">카테고리</label>
					<select id="category" name="productCategory" required>
						<option value="" ${empty product.productCategory ? 'selected' : ''}>카테고리 선택</option>
						<option value="중앙경제평론사" ${product.productCategory eq '중앙경제평론사' ? 'selected' : ''}>중앙경제평론사</option>
						<option value="중앙생활사" ${product.productCategory eq '중앙생활사' ? 'selected' : ''}>중앙생활사</option>
						<option value="중앙에듀북스" ${product.productCategory eq '중앙에듀북스' ? 'selected' : ''}>중앙에듀북스</option>
					</select>
				</div>
	
				<button class="product-cancel-btn" type="button" data-url='<c:url value="/products/list${criteria.params}" />' >취소하기</button>
				<button type="submit" class="product-update-btn" data-update="true">등록하기</button>
			</form>
		</div>
	</main>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>
	let msg = '${msg}';
	if(msg){alert(msg);}
	let contextPath = '${pageContext.request.contextPath}';
	let sessionUserNum = '${sessionScope.userNum}';
</script>
<script src="<c:url value='/static/js/script.js' />"></script>
<script src="<c:url value='/static/js/product-register.js' />"></script>
</html>
