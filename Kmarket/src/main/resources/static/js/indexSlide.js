$(document).ready(function(){
    $(".slider").bxSlider({
        auto: true,        // 자동 슬라이드 활성화
        pause: 3000,       // 각 슬라이드 간의 시간 간격 (3초)
        autoHover: true    // 마우스 호버 시 자동 슬라이드 일시정지
    });
});

$(document).ready(function() {
    // 이전 버튼 클릭 시
    $("#hitProd").click(function(e) {
        e.preventDefault(); // 기본 동작을 중단
        // 이동하고자 하는 요소로 스크롤 이동
        $("html, body").animate({
            scrollTop: $("#hit-span").offset().top
        }, 500); // 이동에 걸리는 시간 (500ms)
    });
});
$(document).ready(function() {
    // 이전 버튼 클릭 시
    $("#recommendProd").click(function(e) {
        e.preventDefault(); // 기본 동작을 중단
        // 이동하고자 하는 요소로 스크롤 이동
        $("html, body").animate({
            scrollTop: $("#recommend-span").offset().top
        }, 500); // 이동에 걸리는 시간 (500ms)
    });
});
$(document).ready(function() {
    // 이전 버튼 클릭 시
    $("#recentProd").click(function(e) {
        e.preventDefault(); // 기본 동작을 중단
        // 이동하고자 하는 요소로 스크롤 이동
        $("html, body").animate({
            scrollTop: $("#recent-span").offset().top
        }, 500); // 이동에 걸리는 시간 (500ms)
    });
});
$(document).ready(function() {
    // 이전 버튼 클릭 시
    $("#discountProd").click(function(e) {
        e.preventDefault(); // 기본 동작을 중단
        // 이동하고자 하는 요소로 스크롤 이동
        $("html, body").animate({
            scrollTop: $("#discount-span").offset().top
        }, 500); // 이동에 걸리는 시간 (500ms)
    });
});

$(function(){
    let best = $("aside > .best");

    $(window).scroll(function(){
        let t = $(this).scrollTop();

        if(t > 620){
            best.css({
                position: "fixed",
                top: "0",
            });
        } else {
            best.css({position: "static"});
        }
    });
});

$(function(){
    let $w = $(window),
        footerHei = $('footer').outerHeight(),
        $banner = $('.best');

    $w.on('scroll', function(){

        let sT = $w.scrollTop();
        let val = $(document).height() - $w.height() - footerHei;

        if(sT >= val)
            $banner.addClass('on')
        else
            $banner.removeClass('on')

    });
});