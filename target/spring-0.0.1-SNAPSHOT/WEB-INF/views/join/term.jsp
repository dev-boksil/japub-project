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
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap"
	rel="stylesheet" />
<link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
<link rel="stylesheet" href="<c:url value='/static/css/term.css' />" />
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
	<main class="main">
	<form name="termForm" method="post" action="<c:url value='/join/term' />">
	  <div class="terms-container">
      <h1>회원가입</h1>
      <div class="">
        <h4 class="terms-title">개인정보처리방침</h4>
        <div class="terms-box">
          <h5>수집하는 개인정보의 항목</h5>
          <p>
            회사는 회원가입, 상담, 서비스 신청 등을 위해 아래와 같은 개인정보를 수집하고 있습니다.
          </p>
          <ul>
            <li>
              <strong>- 수집항목</strong> :
              <span class="privacy_column_list"
                >아이디, 별명, 패스워드, 성명, e-mail, 휴대전화, 생년월일, 주소</span
              >
            </li>
            <li>
              <strong>- 개인정보 수집방법</strong> : <span>홈페이지</span>(<span>회원가입</span>)
            </li>
          </ul>

          <h5>개인정보의 수집 및 이용목적</h5>
          <p>회사는 수집한 개인정보를 다음의 목적을 위해 활용합니다.</p>
          <ul>
            <li>
              <strong>- 서비스 제공에 관한 계약 이행 및 서비스 제공에 따른 요금정산</strong>
              <p>구매 및 요금 결제, 물품배송 또는 청구지 등 발송</p>
            </li>
            <li>
              <strong>- 회원 관리</strong>
              <p>
                회원제 서비스 이용에 따른 본인확인, 개인 식별, 불량회원의 부정 이용 방지와 비인가
                사용 방지, 가입 의사 확인, 연령확인
              </p>
            </li>
          </ul>

          <h5>개인정보의 보유 및 이용기간</h5>
          <p>
            회사는 개인정보 수집 및 이용목적이 달성된 후에는 예외 없이 해당 정보를 지체 없이
            파기합니다.
          </p>
        </div>

        <div class="terms-checkbox">
          <input
            type="checkbox"
            name="termAgree"
            id="term-agree"
            value="개인정보처리방침"
          />
          <label for="term-agree">위 개인정보처리방침에 동의합니다.</label>
        </div>
      </div>
      <div class="terms-button">
        <a href="<c:url value='/main' />" class="termCancelBtn">이전으로</a>
        <a href="<c:url value='/join' />" class="termSubmitBtn">회원가입</a>
      </div>
    </div>
    </form>
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
<script src="<c:url value='/static/js/term.js' />"></script>

</html>