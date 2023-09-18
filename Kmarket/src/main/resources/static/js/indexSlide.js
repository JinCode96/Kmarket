// index
$(document).ready(function(){
    $(".slider > ul").bxSlider({
        easing: "linear",
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