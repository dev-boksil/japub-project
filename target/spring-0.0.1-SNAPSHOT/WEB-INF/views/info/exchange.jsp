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
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
		<main class="main">
	<table width="50%" cellpadding="0" cellspacing="0" align="center" style="margin: 4vh auto">
      <tbody>
        <tr>
          <td>
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
              <tbody>
                <tr>
                  <td align="middle" colspan="2">
                    <div>
                      <img alt="환영합니다" src="<c:url value='/static/images/logo/main-logo.jpg' />" width="300" style="margin-right: 50px" />
                    </div>
                    <b>배송비용 및 교환안내</b><br />
                    <br />
                  </td>
                </tr>
                <tr>
                  <td align="left" style="text-align: center">
                    <span class="txt-n">
                      중앙북샵을 찾아주신 여러분을 진심으로 환영합니다. <br />
                      이 페이지는 고객님의 보다 편리한 쇼핑을 돕고자, 이용 안내에 대한 기본적인
                      내용을 아래의 그림과 함께 마련하였습니다.<br />
                      그밖에
                      <font color="#CC3300"
                        ><b>중앙북샵 이용에 대한 전반적인 내용및 문의 사항</b></font
                      >
                      은 상단 우측의 게시판 &gt;
                      <b><a class="txt-a" href="./qa.php?qa_table=es_qa">문의게시판</a></b>
                      을 참조해 주세요.
                    </span>
                  </td>
                </tr>
                <tr>
                  <td colspan="2" style="padding: 5px 0">
                    <hr color="#400080" size="1" width="100%" />
                  </td>
                </tr>
              </tbody>
            </table>
            <table cellpadding="0" width="100%" border="0" align="center">
              <tbody>
                <tr>
                  <td width="10"></td>
                  <td class="txt-n" style="text-align: center">
                    <span class="tie-1">
                      <font color="#CC6666">
                        <b>
                          <font size="4">
                            <font size="3" color="#CC3300">배송비용 및 교환</font>
                          </font>
                        </b>
                      </font>
                    </span>
                    <br /><br />
                    <span class="txt-p">
                      반품처리 후 <font color="#CC3300"><b>교환</b></font
                      >해 드립니다. 이에 소요되는
                      <font color="#CC3300"><b>재발송 비용은 본사가 부담</b></font
                      >합니다. </span
                    ><br /><br />
                    <span class="tie-2">
                      <font size="3"
                        ><b><font size="2">반송방법</font></b></font
                      >
                    </span>
                    <ul>
                      <li>
                        <span class="tie-3">
                          <font color="#CC3300"><b>등기를 통한 반송</b></font> </span
                        ><br /><br />
                        아래와 같은 주소로 오발송/반품할 도서를 보내주시면 됩니다.<br />
                        <dd>
                          <b
                            >주소 : (우편번호 100-826) 서울시 중구 다산로20길 5(신당4동 340-128) 1층
                            중앙경제평론사 반송담당자</b
                          >
                        </dd>
                      </li>
                      <li>
                        <span class="tie-3"
                          ><font color="#CC3300"><b>반송하실 때</b></font></span
                        ><br /><br />
                        파본 도서의 경우 구체적인 파본 부분을 포스트잇이나 메모지를 이용해
                        표시해주시기 바랍니다.<br />
                        또한 구매처를 알려 주시면 주문자 확인 등에 소요되는 시간을 단축할 수 있어,
                        환불이나 교환 등의 사후처리를 보다 빨리 시행할 수 있습니다.<br /><br />
                      </li>
                      <li>
                        <span class="tie-3"
                          ><font color="#CC3300"
                            ><b>재배송 비용은 본사에서 부담합니다.</b></font
                          ></span
                        ><br /><br />
                        <span class="tie-3">재배송 비용은 본사에서 부담합니다.</span> 다만, 책을
                        보내실 때 구매처도 함께 써서 보내주세요.
                      </li>
                    </ul>
                  </td>
                </tr>
              </tbody>
            </table>
            <br />
          </td>
        </tr>
      </tbody>
    </table>
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