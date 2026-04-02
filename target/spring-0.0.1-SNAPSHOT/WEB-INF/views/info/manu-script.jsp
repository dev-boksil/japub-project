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
<link rel="stylesheet" href="<c:url value='/static/css/manu-script.css' />" />
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
	<main class="main">
	<div class="manuscript">
      <div id="receipt" style="margin: 0, 0, -10, 0; font-weight: bold">
        <h3> @ 원고모집 </h3>
        <br/>
      </div>
      <pre>
        경제·경영·재테크와 건강·자연과학·실용서 전문 출판으로 독자들에게 실용적인 지식 전달을 위해 
        최선을 다하고 있는 중앙경제평론사와 중앙생활사에서 여러분의 소중한 원고(집필/번역)를 모집합니다.
        
        참신한 번역 아이템이나 훌륭한 저술원고가 있으면 언제든지 저희에게 문의해 주십시오.
        꼼꼼히 검토하여 출간 여부를 알려드리겠습니다. 
        
        출간이 결정된 원고는 최선을 다해 한 권의 책으로 만들어 저자분의
        노고에 보답하도록 하겠습니다.
        </pre>
      <p>
        <span id="fps0">*</span><b> 원고검토 기간 :</b> 약 3~5일(검토 후 출간여부 통보) <br /><br />
        <span id="fps0">*</span><b> 모집분야 :</b> 1) 경제 / 경영 / 자기계발 / 재테크 / 창업 등
        <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        2) 건강 / 취미 / 자연과학 / 기타 생활실용서 <br /><br />
        <span id="fps0">*</span><b> 모집방법 :</b> 1) 원고는 ‘한글’이나 'MS 워드’ 문서로 작성해
        이메일로 전송해 주십시오.
        <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        &nbsp;&nbsp;2) 이메일이 여의치 않을 경우 원고 복사본을 등기우편으로 보내주십시오.
        <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)
        원고를 보내실 때는 간단한 저자약력과 연락처(전화, 이메일)를 반드시 표기해 주십시오.
        <br /><br />
        <span id="fps0">*</span><b> 문의 및 원고 보낼 곳</b> <br />&nbsp;&nbsp;&nbsp;&nbsp;이메일 :
        japub@naver.com<br />&nbsp;&nbsp;&nbsp;&nbsp;전화 : 02) 2253-4463
        <br />&nbsp;&nbsp;&nbsp;&nbsp;주소 : 서울시 중구 다산로20길 5(신당4동 340-128) 우편번호
        100-826 <br />&nbsp;&nbsp;&nbsp;&nbsp;홈페이지 : www.japub.co.kr
      </p>
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
</html>