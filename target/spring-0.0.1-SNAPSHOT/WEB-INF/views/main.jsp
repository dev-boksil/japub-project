<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <!-- <meta name="viewport" content="width=device-width, initial-scale=1.0" /> -->
  <meta name="naver-site-verification" content="1b43d1a484c9c292a604407939f25d7af1e3b57d" />
  <title>Insert title here</title>
  <link rel="icon" href="<c:url value='/static/images/logo/favicon.png' />" />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
  <link rel="stylesheet" href="<c:url value='/static/css/main.css' />" />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
</head>
<body>
  <jsp:include page="/WEB-INF/views/layout/header.jsp" />
  <main class="main">
    <section class="slide-section">
      <div class="slide-container">
        <div id="slide-swiper" class="swiper">
          <div class="swiper-wrapper">
            <div class="swiper-slide">
              <img class="slide-img" src="<c:url value='/static/images/slide/slide1.jpg' />" />
            </div>
            <div class="swiper-slide">
              <img class="slide-img" src="<c:url value='/static/images/slide/slide2.jpg' />" />
            </div>
            <div class="swiper-slide">
              <img class="slide-img" src="<c:url value='/static/images/slide/slide3.jpg' />" />
            </div>
            <div class="swiper-slide">
              <img class="slide-img" src="<c:url value='/static/images/slide/slide4.jpg' />" />
            </div>
          </div>
          <div class="slide-img" class="swiper-pagination"></div>
        </div>
      </div>
    </section>

    <section class="book-section">
      <div class="book-container">
        <div class="book-title">
          <h1>신간도서</h1>
          <div class="book-title-right">
            <a href="<c:url value='/products/list?sort=recent' />">더보기 +</a>
          </div>
        </div>
        <div id="book-swiper" class="swiper">
          <div class="swiper-wrapper book-wrapper">
          	<c:forEach items="${newProducts}" var="newProduct">
          		
            		<div class="swiper-slide">
            			<a target="_blank" href="${newProduct.productUrl}">
            				<img src="<c:url value='/upload/products/${newProduct.productThumbnailPath}'/>"  width="100%"/>
            			</a>
            		</div>
            </c:forEach>
          </div>
          <button type="button" class="swiper-button-prev type_white swiper-button-disabled" tabindex="0" role="button" aria-label="Previous slide" aria-disabled="true"></button>
          <button type="button" class="swiper-button-next type_white" tabindex="0" role="button" aria-label="Next slide" aria-disabled="false"></button>
        </div>
      </div>
    </section>

    <section class="book-section">
      <div class="book-container">  
      <form name="deleteRecommendForm" method="post" action="<c:url value='/products/recommend/cancel' />">
      	<input type="hidden" name="productNum" value=""/>
  	  </form>
        <div class="book-title">
          <h1>추천도서</h1>
          <div class="book-title-right">
            <a href="<c:url value='/products/list' />">더보기 +</a>
          </div>
        </div>
        <div id="book-swiper" class="swiper">
          <div class="swiper-wrapper book-wrapper">
          	<c:forEach items="${recommendProducts}" var="recommendProduct">
            	<div class="swiper-slide">
            		<c:if test="${sessionScope.isAdmin}">
	            		<div class="item-actions">
				            <a href="<c:url value='/products/delete' />" class="recommend-cancel-btn" data-product-num="${recommendProduct.productNum}">삭제</a>
		          		</div>
	          		</c:if>
	          		<a href="${recommendProduct.productUrl}">
            			<img src="<c:url value='/upload/products/${recommendProduct.productThumbnailPath}' />" data-file-num="${recommendProduct.productNum}" width="100%"  />
            		</a>
            	</div>
            </c:forEach>
          </div>
          <button type="button" class="swiper-button-prev type_white swiper-button-disabled" tabindex="0" role="button" aria-label="Previous slide" aria-disabled="true"></button>
          <button type="button" class="swiper-button-next type_white" tabindex="0" role="button" aria-label="Next slide" aria-disabled="false"></button>
        </div>
      </div>
    </section>

    <section class="layout-section">
      <div class="layout-container">
        <div class="layout-box">
          <div class="video">
            <div class="video-header">
              <h2>중앙 TV</h2>
              <a href="https://www.youtube.com/@joongangpub" target="_blank" class="video-btn notice-btn">+</a>
            </div>
            <div class="video-content">
              <iframe src="https://www.youtube.com/embed/zdD80ti_JPI?si=uTqSVYl9gTll0CcZ&loop=1&mute=1&autoplay=1"
                      title="YouTube video player"
                      frameborder="0"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                      referrerpolicy="strict-origin-when-cross-origin"
                      allowfullscreen>
              </iframe>
            </div>
          </div>
        </div>

        <div class="layout-box">
          <div class="notice">
            <div class="notice-header">
              <h2>미디어 리뷰</h2>
              <a href="<c:url value='/board/list?category=media' />" class="notice-btn">+</a>
            </div>
            <div class="notice-body">
              <ul>
                <c:forEach items="${mediaBoards}" var="media" begin="0" end="4">
                  <li>
                  	  <c:set var="referrer" value="main"/>
	                  <a href="<c:url value='/board/detail?category=${media.boardCategory}&referrer=${referrer}&boardNum=${media.boardNum}' />">
	                      <div class="notice-content">${media.boardTitle}</div>
	                      <div class="notice-date">${media.boardRegisterDate}</div>
	                  </a>
                  </li>
                </c:forEach>
              </ul>
            </div>
          </div>
        </div>

        <div class="layout-box">
          <div class="notice">
            <div class="notice-header">
              <h2>자료실</h2>
              <a href="<c:url value='/board/list?category=download' />" class="notice-btn">+</a>
            </div>
            <div class="notice-body">
              <ul>
                <c:forEach items="${downloadBoards}" var="download" begin="0" end="4">
                  <li>
                    <a href="<c:url value='/board/detail?category=${download.boardCategory}&referrer=${referrer}&boardNum=${download.boardNum}' />">
                      <div class="notice-content">${download.boardTitle}</div>
                      <div class="notice-date">${download.boardRegisterDate}</div>
                    </a>
                  </li>
                </c:forEach>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>

  <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
 <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
 <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script src="<c:url value='/static/js/main.js' />"></script>
<script>
  let msg = '${msg}';
  if (msg) { alert(msg); }
  let contextPath = '${pageContext.request.contextPath}';
  const sessionUserNum = '${sessionScope.userNum}';
</script>
<script src="<c:url value='/static/js/script.js' />"></script>

</html>
