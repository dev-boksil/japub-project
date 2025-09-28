<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
  <link rel="stylesheet" href="<c:url value='/static/css/product-list.css' />" />
</head>
<body>
  <jsp:include page="/WEB-INF/views/layout/header.jsp"/>
  <main class="main">
    <div class="content">
      <nav class="products-category">
        <ul class="category-list">
          <li class="product-menu"><a class="${empty criteria.category ? 'category-active' : ''}" href="<c:url value='/products/list' />">전체상품</a></li>
          <li class="product-menu"><a class="${criteria.category == '중앙경제평론사' ? 'category-active' : ''}" href="<c:url value='/products/list?category=중앙경제평론사' />">중앙경제평론사</a></li>
          <li class="product-menu"><a class="${criteria.category == '중앙생활사' ? 'category-active' : ''}" href="<c:url value='/products/list?category=중앙생활사' />">중앙생활사</a></li>
          <li class="product-menu"><a class="${criteria.category == '중앙에듀북스' ? 'category-active' : ''}" href="<c:url value='/products/list?category=중앙에듀북스' />">중앙에듀북스</a></li>
        </ul>
      </nav>
      
     <div class="product-search-container">
  		<form name="productSearchForm" method="get" action="<c:url value='/products/list' />">
  			<input type="hidden" name="category" value="${criteria.category}"  />
    		<input type="text" name="keyword" value="${criteria.keyword}" placeholder="검색어를 입력하세요" />	
    		<button type="submit" class="product-search-btn">검색</button>
  		</form>
	 </div>

      <div class="sort-filter">
        <ul class="sortkey-filter">
          <li class="sort-key ${criteria.sort == 'recent' ? 'sort-active' : ''}"><a href="recent">최신순</a></li>
          <li class="sort-key ${criteria.sort == 'low_price' ? 'sort-active' : ''}"><a href="low_price">낮은 가격순</a></li>
          <li class="sort-key ${criteria.sort == 'high_price' ? 'sort-active' : ''}"><a href="high_price">높은 가격순</a></li>
        </ul>
      </div>

      <ul class="product-list">
        <c:forEach items="${products}" var="product">
          <li class="item" data-product-num="${product.productNum}" data-is-recommend="${product.productIsRecommend}">
          	<c:if test="${sessionScope.isAdmin}">
		          <div class="item-actions">
		            <a href="<c:url value='/products/recommend/add${criteria.params}&productNum=${product.productNum}' />" class="product-recommend-btn">추천 지정</a>
		            <a href="<c:url value='/products/update${criteria.params}&productNum=${product.productNum}' />" class="product-update-btn">수정</a>
		            <a href="<c:url value='/products/delete${criteria.params}&productNum=${product.productNum}' />" class="product-delete-btn">삭제</a>
		          </div>
		    </c:if>
            <a target="_blank" href="${product.productUrl}">
              <img src="<c:url value='/upload/products/${product.productThumbnailPath}'/>" alt="${product.productTitle}"/>
              <div class="title">${product.productTitle}</div>
              <div class="price">        
                <span class="sale"><fmt:formatNumber value="${product.productDiscountPrice}" pattern="#,###"/>원</span>
                <span class="original"><del><fmt:formatNumber value="${product.productPrice}" pattern="#,###"/>원</del></span>
                <span class="discount">10%</span>
              </div>
            </a>
            <div class="item-footer">
              <span>무료배송</span>
              <span>오늘출발</span>
            </div>
          </li>
        </c:forEach>
      </ul>
    </div>

    <div class="page-div">
      <ul>
        <c:if test="${pageDto.prev}">
          <li><a href="${pageDto.startPage - criteria.pageRange}" class="page">&lt</a></li>
        </c:if>
        <c:forEach begin="${pageDto.startPage}" end="${pageDto.endPage}" var="i">
          <c:choose>
            <c:when test="${criteria.page eq i}">
              <li><a href="${i}" class="page" style="color:blue; font-weight: bold;">${i}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="${i}" class="page">${i}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <c:if test="${pageDto.next}">
          <li><a href="${pageDto.endPage + 1}" class="page">&gt</a></li>
        </c:if>
      </ul>
    </div>

    <div>
      <form name="productForm" method="get" action="<c:url value='/products/list'/>">
        <input type="hidden" name="page" value="${criteria.page}" />
        <input type="hidden" name="sort" value="${criteria.sort}" />
        <input type="hidden" name="category" value="${criteria.category}" />
        <input type="hidden" name="keyword" value="${criteria.keyword}" />
      </form>
    </div>
  </main>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>

<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>
  let msg = '${msg}';
  if (msg) {
    alert(msg);
  }
  let contextPath = '${pageContext.request.contextPath}';
  let sessionUserNum = '${sessionScope.userNum}';
</script>
<script src="<c:url value='/static/js/script.js' />"></script>
<script src="<c:url value='/static/js/product-list.js' />"></script>
</html>
