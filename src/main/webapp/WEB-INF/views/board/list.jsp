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
<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap"
	rel="stylesheet" />
<link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
<link rel="stylesheet" href="<c:url value='/static/css/board-list.css' />" />
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
	<main class="main">
			<div class="list-container">
				    <table>
						<c:if test="${writable}">
							<caption>
								<a href="<c:url value='/board/write${criteria.params}' />">글쓰기</a>
							</caption>
						</c:if>
						<tr>
							<th>게시글번호</th>
							<th>작성자</th>
							<th>제목</th>
							<th>등록일</th>
							<th>조회수</th>
						</tr>
						<c:forEach items="${boards}" var="board">
							<tr>
								<td>${board.boardNum}</td>
								<td>${board.userId}</td>
								<td class="boardTitle"><a href="<c:url value='/board/detail${criteria.params}&boardNum=${board.boardNum}' />">${board.boardTitle}</a></td>
								<td>${board.boardRegisterDate}</td>
								<td>${board.boardReadCount}</td>
							</tr>
						</c:forEach>
					</table>
					
					<div class="search-form-div">
						<form name="searchForm">
							<select name="type">
								<option value="" ${empty criteria.type ? 'selected' : ''}>선택하세요</option>
								<option value="T" ${criteria.type eq 'T' ? 'selected' : ''}>제목</option>
								<option value="C" ${criteria.type eq 'C' ? 'selected' : ''}>내용</option>
								<option value="TC" ${criteria.type eq 'TC' ? 'selected' : ''}>제목+내용</option>
								<option value="I" ${criteria.type eq 'I' ? 'selected' : ''}>작성자</option>
							</select>
							<input type="text" name="keyword" value="${criteria.keyword}" />
							<input type="submit" value="검색" />
							<input type="hidden" name="page" value="${criteria.page}" />
							<input type="hidden" name="category" value="${criteria.category}" />
						</form>
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
					
					<div class="page-form-div">
						<form name="pageForm">
							<input type="hidden" name="page" value="${criteria.page}" />
							<input type="hidden" name="type" value="${criteria.type}" />
							<input type="hidden" name="keyword" value="${criteria.keyword}" />
							<input type="hidden" name="category" value="${criteria.category}" />
						</form>
					</div>
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
<script type="module" src="<c:url value='/static/js/board.js' />"></script>
</html>