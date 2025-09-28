<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
    <link rel="icon" href="<c:url value='/static/images/logo/favicon.png' />" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
    <link rel="stylesheet" href="<c:url value='/static/css/board-write.css' />" />
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />
    <main class="main">
        <form name="registerForm" class="write-form" method="post" autocomplete="off">
            <div class="container" data-board-category="${criteria.category}">
                <div class="header-with-buttons">
                    <h1 class="title">
                        <input type="text" class="title-input" name="boardTitle" placeholder="제목을 입력하세요" maxlength="55" value="${board.boardTitle}"/>
                    </h1>
                    <div class="btn-group">
                        <a href="<c:url value='/board/list${criteria.params}' />" class="btn"><i class="fa fa-bars"></i> 취소</a>
                        <a href="#" class="btn write-board-btn" data-form-class="write-form"><i class="fa fa-pencil"></i> 등록</a>
                    </div>
                </div>

                <div class="post-info">
                    <span><i class="fa fa-user-circle"></i><c:out value="${userId}" /></span>
                </div>
                <c:set var="category" value="media"/>
                <c:if test="${sessionScope.isAdmin and criteria.category eq category}">
	                <div class="link-url-box">
				    	<input type="text" name="boardLinkUrl" class="board-link-input" placeholder="이동 할 링크 (생략가능)"/>
	        		</div>
	                <div class="video-id-box">
				    	<input type="text" name="boardVideoId" class="video-id-input" placeholder="유튜브 영상 ID (생략가능)"/>
	        		</div>
        		</c:if>
                <article class="post-content">
                    <div class="board-content">
                        <textarea name="boardContent" placeholder="내용을 입력하세요."><c:out value="${board.boardContent}" /></textarea>
                    </div>
                    <div class="board-content-line"></div>
                </article>
 				<input type="file" name="multipartFiles" multiple />
	            <div class="thumbnail-div">
		            <ul class="thumbnail-ul"></ul>
		        </div>   
            </div>   
        </form>
    </main>
    <div class="dimmed-container" role="status" aria-live="polite" aria-busy="true">
	    <img class="dimmed" src="<c:url value='/static/images/file/dimmed.svg'/>" alt="loading">
	</div>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>
    let msg = '${msg}';
    if (msg) { alert(msg); }
    let contextPath = '${pageContext.request.contextPath}';
    let sessionUserNum = '${sessionScope.userNum}';
</script>
<script src="<c:url value='/static/js/script.js' />"></script>
<script src="<c:url value='/static/js/file.js' />"></script>
<script src="<c:url value='/static/js/board.js' />"></script>
</html>
