/*let submenuItems = document.querySelectorAll('ul.submenu');
let heightArray = new Array(submenuItems.length);

submenuItems.forEach((submenu, i) => {
	heightArray[i] = submenu.offsetHeight;
	submenu.style.height = '0px';
	submenu.style.visibility = 'visible';
});

document.querySelectorAll('.header>nav>ul>li').forEach((li, i) => {
	li.addEventListener('mouseover', () => {
		submenuItems[i].style.height = `${heightArray[i]}px`;
	});
	li.addEventListener('mouseout', () => {
		submenuItems[i].style.height = '0px';
	});
});
*/

(function() {

	$(document).ready(function() {
		let submenuItems = $('ul.submenu');
		let heightArray = submenuItems.map(function() {
			return $(this).outerHeight();
		}).get();

		submenuItems.css('height', '0px').css('visibility', 'visible');

		function addMenuEvents(isMobile) {
			$('.header>nav>ul>li').each(function(i) {
				if (isMobile) {
					// 모바일에서 터치 이벤트
					$(this).on('touchstart', function() {
						submenuItems.eq(i).css('height', `${heightArray[i]}px`);
					});
					$(this).on('touchend', function() {
						submenuItems.eq(i).css('height', '0px');
					});
				} else {
					// PC에서 마우스 이벤트
					$(this).on('mouseover', function() {
						submenuItems.eq(i).css('height', `${heightArray[i]}px`);
					});
					$(this).on('mouseout', function() {
						submenuItems.eq(i).css('height', '0px');
					});
				}
			});
		}

		// 화면 크기에 따라 이벤트 핸들러 변경
		$(window).width() <= 768 ? addMenuEvents(true) : addMenuEvents(false);

		// 화면 크기 변경시 이벤트 핸들러 재설정
		$(window).on('resize', function() {
			$(window).width() <= 768 ? addMenuEvents(true) : addMenuEvents(false);
		});
	});

	$("header > h1").on("click", function(e) { /*로고클릭시 메인화면으로*/
		e.preventDefault();
		location.href = contextPath + "/main";
	})

	$("form").on("submit", function(e) {
		const $form = $(this);
		if ($form.data("submitted")) {
			e.preventDefault(); // ← 제출 막기
			return;
		}
		$form.data("submitted", true); // ← 제출 허용됨
	});

	$("a.menu-link").on("click", function(e) {
		e.preventDefault();
	});
})();



