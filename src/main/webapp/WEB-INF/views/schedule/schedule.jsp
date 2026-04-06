<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>중앙경제평론사</title>
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap"
	rel="stylesheet" />
<link rel="stylesheet" href="<c:url value='/static/css/style.css' />" />
<link rel="stylesheet" href="<c:url value='/static/css/schedule.css' />" />
<link rel="stylesheet" href="<c:url value='/static/css/modal.css' />" />
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
		<main class="main">
		<jsp:include page="/WEB-INF/views/schedule/modal.jsp" />
		  <div class="schedule-wrap">
             <div class="tit_bar">
                    <h2>스케쥴 관리</h2>
                    <span>스케쥴을 관리하는 공간입니다.</span>
                  </div>
              <div class="date_wrap">
                <div class="date_select">
                  <img class="prev-btn" src="<c:url value='/static/images/schedule/btn_prev.png' />" />
                  <span class="date-range"></span>
                  <img class="next-btn" src="<c:url value='/static/images/schedule/btn_next.png' />" />
                </div>
                <div class="position-absolute end-0">
                  <button class="btn_today">오늘</button>
                  <button id="btnInputDate" class="btn_input_date">날짜 입력</button>
                </div>
              </div>

              <div class="legend_wrap">
                <div class="legend_item"><span class="item_1"></span> 예약 가능</div>
                <div class="legend_item"><span class="item_2"></span> 영업마감</div>
                <div class="legend_item"><span class="item_3"></span> 대관</div>
                <div class="legend_item"><span class="item_4"></span> 레슨</div>
                <div class="legend_item"><span class="item_5"></span> 외부레슨</div>
              </div>

                  <table class="schedule-table">
                    <thead>
                      <tr class="thead-date"></tr>
                    </thead>
                    <tbody class="tbody">

                    </tbody>
                  </table>
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
	let isAdmin = '${isAdmin}';
</script>
<script src="<c:url value='/static/js/script.js' />"></script>
<script src="<c:url value='/static/js/schedule.js' />"></script>
</html>