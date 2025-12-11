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
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
  <link rel="stylesheet" href="<c:url value='/static/css/mypage-update.css' />" />
</head>
<body>
  <jsp:include page="/WEB-INF/views/layout/header.jsp" />
  <main class="main">
    <div class="join-box">
      <form:form name="joinForm" autocomplete="off" modelAttribute="userDto" method="post" class="mypageForm">
        <div class="join-header">
          <span class="title">마이페이지</span>
        </div>

        <div>
          <input type="hidden" name="userNum" value="${user.userNum}" />
        </div>

        <div class="join-input-div">
          <input type="text" name="userId" placeholder="아이디" autofocus value="${user.userId}" readonly="readonly" />
          <span class="error-msg"></span>
        </div>

        <div class="join-input-div">
          <input type="password" name="userPassword" placeholder="비밀번호" value="${user.userPassword}" readonly="readonly" autocomplete="off" />
          <span class="error-msg"></span>
        </div>

        <div class="join-input-div">
          <input type="hidden" name="userPasswordCheck" placeholder="비밀번호 재입력" value="" />
          <span class="error-msg"></span>
        </div>

        <div class="join-input-div">
          <input type="text" name="userPhone" placeholder="핸드폰번호" value="${user.userPhone}" readonly="readonly" />
          <span class="error-msg"></span>
        </div>

        <div class="join-input-div">
          <input type="text" name="userEmail" placeholder="이메일" value="${user.userEmail}" readonly="readonly" />
        </div>

        <div class="find-address">
          <div>
            <input type="text" id="sample6_postcode" name="userZipCode" placeholder="우편번호" value="${user.userZipCode}" readonly="readonly" />
            <input type="button" onclick="" value="검색" />
          </div>
        </div>

        <div class="join-input-div">
          <input type="text" id="sample6_address" name="userAddress" placeholder="주소" value="${user.userAddress}" readonly="readonly" />
        </div>

        <div class="join-input-div">
          <input type="text" id="sample6_detailAddress" name="userDetailAddress" placeholder="상세주소" value="${user.userDetailAddress}" readonly="readonly" />
        </div>

        <div class="join-input-div">
          <input type="button" class="cancelChangePwBtn" value="취소" />
        </div>

        <div class="join-input-div">
          <input type="button" class="changePwBtn" value="수정하기" />
          <input type="submit" class="changePwOkbtn" value="변경완료" />
          <%-- <input type="button" class="user-delete-btn" data-url="<c:url value='/mypage/delete' />" value="회원탈퇴" /> --%>
        </div>
      </form:form>
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
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script type="module" src="<c:url value='/static/js/join.js' />"></script>
<script type="module" src="<c:url value='/static/js/mypage-update.js' />"></script>
</html>