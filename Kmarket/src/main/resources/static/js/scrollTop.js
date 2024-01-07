$(document).ready(function() {
    // 최상단 이동 버튼 클릭 시
    $("#top").click(function() {
        // 페이지 최상단으로 스크롤 이동
        $("html, body").animate({
            scrollTop: 0
        }, 500); // 이동에 걸리는 시간
    });
});