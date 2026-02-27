<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>중앙경제평론사</title>
<link rel="icon" href="<c:url value='/static/images/logo/favicon.png' />" />
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap"
	rel="stylesheet" />
<link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
<link rel="stylesheet" href="<c:url value='/static/css/login.css' />" />
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
		<main class="main">
			<div class="login-box">
			      <header><img src="<c:url value='/static/images/logo/login-logo.jpg' />" width="60" height="60"/></header>
			      <form name="loginForm" action='<c:url value="/login"/>' method="post">
				        <input type="text" name="userId" placeholder="아이디" value="${cookie.id.value}"/>
				        <input type="password" name="userPassword" placeholder="비밀번호" />
				        <input type="hidden" name="toUri" value="${criteria.toUri}" />
				        <input type="hidden" name="type" value="${criteria.type}" />
				        <input type="hidden" name="keyword" value="${criteria.keyword}" />
				        <input type="hidden" name="category" value="${criteria.category}" />
				        <input type="hidden" name="sort" value="${criteria.sort}" />
				        <input type="hidden" name="boardNum" value="${param.boardNum}" />
				        <input type="hidden" name="productNum" value="${param.productNum}" />
				        <input type="submit" value="로그인" />
				        <div>
				          <label> <input type="checkbox" name="rememberId"  ${empty cookie.id.value ? '' : 'checked'}/>&nbsp아이디 기억 </label>
				          &nbsp;|&nbsp;<a href="<c:url value='/find-account' />">아이디 또는 비밀번호 찾기</a>&nbsp;|&nbsp;<a href="<c:url value='/join/term' />">회원가입</a>
				       	</div>
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
<script src="<c:url value='/static/js/login.js' />"></script>
</html>