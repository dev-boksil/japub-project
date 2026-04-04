<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <div class="custom_modal_overlay">
       <input type="hidden" name="scheduleNum" value=""/>
      <div class="custom_modal">
        <div class="custom_modal_header">
          <h5>스케줄 수정 / 삭제</h5>
          <button class="btn_close">×</button>
        </div>
        <div class="custom_modal_body">
          <p>
            <strong>날짜 시간:</strong>
            <strong class="reservationDate"></strong>
            <input type="hidden" name="reservationDate" />
          </p>
          <div class="schedule_state_section">
            <label
              >일정 상태
              <select id="schedule_state" name="scheduleState">
                <option value="" >카테고리를 선택하세요</option>
                <option value="RENTAL" >대관</option>
                <option value="LESSON">레슨</option>
                <option value="EXTERNAL">외부레슨</option>
                <option value="CLOSED" >영업마감</option>
              </select>
            </label>
          </div>

          <div class="price_section">
            <label>가격
              <input type="number" class="form-control" name="schedulePrice" value=""/>
            </label>
          </div>

          <div class="desc_section">
            <label
              >내용
              <input type="text" id="description" name="scheduleContent" class="form-control" value="${schedule.scheduleContent}"
            /></label>
          </div>
        </div>
        <div class="custom_modal_footer">
          <!— 기본 버튼들 —>
          <div class="footer_main_buttons">
            <button class="modal-register btn_update">등록</button>
            <button class="modal-update btn_update">수정</button>
            <button class="btn_delete">삭제</button>
            <button class="btn_close">닫기</button>
          </div>

          <!— 이후 일정 적용 버튼들 (처음엔 숨김) —>
          <div class="footer_apply_buttons" style="display: none">
            <button class="btn_apply_all">이후 일정도 적용</button>
            <button class="btn_apply_one">선택 일정만 적용</button>
            <button class="btn_back">뒤로</button>
          </div>
        </div>
      </div>
    </div>
