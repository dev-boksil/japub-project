<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Insert title here</title>
  <link rel="icon" href="<c:url value='/static/images/logo/favicon.png' />" />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
  <link rel="stylesheet" href="<c:url value='/static/css/find-account.css' />" />
</head>
<body>
  <jsp:include page="/WEB-INF/views/layout/header.jsp" />
  <main class="main">
    <div class="find-container">
      <div class="find-header">
        <h1>아이디&amp;비밀번호 찾기</h1>
      </div>

      <div id="find-contents" class="find-contents">
        <form name="find-user-form" method="post" style="margin: 0">
          <p class="find-txt">띄어쓰기 및 대소문자 구분해서 정확하게 입력해주세요.</p>

          <fieldset>
            <legend class="glores-A-blind">기본 정보 입력</legend>
            <ul class="find-form-ul">
              <li>
                <label for="email">이메일</label>
                <input type="text" name="userEmail" class="find-input-txt" style="width: 150px" />
              </li>
            </ul>
          </fieldset>

          <fieldset>
            <legend class="glores-A-blind">수신 방법</legend>
            <h2 class="find-title">수신 방법</h2>
            <div class="find-radio">
              <input type="radio" value="email" checked />
              <label for="sendtype2">E-Mail</label>
            </div>
          </fieldset>
        </form>
      </div>

      <div class="find-footer">
        <a href="<c:url value='/login' />" class="find-btn">취소</a>
        <a href="" class="find-btn find-btn-right">찾기</a>
      </div>
    </div>
  </main>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>

<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>
  let msg = '${msg}';
  if (msg) {alert(msg);}
  let contextPath = '${pageContext.request.contextPath}';
  let sessionUserNum = '${sessionScope.userNum}';
</script>
<script src="<c:url value='/static/js/script.js' />"></script>
<script src="<c:url value='/static/js/find-account.js' />"></script>
</html>