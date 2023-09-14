// aside 리스트 하위항목 숨기기 및 보이기 기능
$(function(){
    $(document).on('click', '#gnb > li', function(){
        $('#gnb > li').not($(this)).children('ol').slideUp(200);
        let display = $(this).children('ol').css('display');
        if(display == 'block'){
            $(this).children('ol').slideUp(200);
        } else {
            $(this).children('ol').slideDown(200);
        }
    });
})