document.addEventListener('DOMContentLoaded', function () {
    // 초기 상품 가격 및 할인된 가격 가져오기
    let discountedPrice = parseInt(document.querySelector('.dis_price ins').innerText.replace(/[^0-9]/g, ''));

    // 초기 상품 개수 가져오기
    let quantityInput = document.querySelector('input[name="num"]');
    let quantity = parseInt(quantityInput.value);

    // 감소 버튼 클릭 시
    document.querySelector('.decrease').addEventListener('click', function () {
        if (quantity > 1) {
            quantity--;
            updateQuantityAndTotal();
        }
    });

    // 증가 버튼 클릭 시
    document.querySelector('.increase').addEventListener('click', function () {
        if (quantity < 100) {
            quantity++;
            updateQuantityAndTotal();
        }
    });

    // 상품 개수 변경 시
    quantityInput.addEventListener('change', function () {
        let inputQuantity = parseInt(quantityInput.value);
        if (!isNaN(inputQuantity) && inputQuantity >= 1 && inputQuantity <= 100) {
            quantity = inputQuantity;
        } else if (inputQuantity < 1) {
            quantity = 1;
            quantityInput.value = 1;
        } else {
            quantity = 100;
            quantityInput.value = 100;
            alert("선택 가능한 수량은 100개입니다.");
        }
        updateQuantityAndTotal();
    });

    // 업데이트 함수 정의
    function updateQuantityAndTotal() {
        quantityInput.value = quantity;
        // 총 상품 가격 계산
        let totalPrice = quantity * discountedPrice;
        // 총 상품 가격 업데이트
        document.querySelector('.totalPrice').innerText = totalPrice.toLocaleString();
    }

    ////////////////////////// 장바구니 추가 ///////////////////////////

    document.getElementById('cartBt').addEventListener('click', function () {
        let productId = encodeURIComponent(this.getAttribute('productId')); // url 인코딩

        /**
         * 상품 장바구니에 추가
         */
        $.ajax({
            url: '/kmarket/product/view',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({"productId" : productId, "quantity": quantity}),
            success: function (data) {
                if (data.status === 200) {
                    if (confirm(data.message)) {
                        window.location.href = '/kmarket/product/cart';
                    }
                } else if (data.status === 400) {
                    alert(data.message);
                } else if (data.status === 600) {
                    window.location.href = '/kmarket/member/login';
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                alert("잘못된 요청입니다. 다시 실행해주세요.");
            }
        });
    });

    ////////////////// 주문하기 페이지로 보내기 ////////////////////

    document.getElementById('orderBt').addEventListener('click', function () {
        let productId = encodeURIComponent(this.getAttribute('productId')); // url 인코딩

        $.ajax({
            url: '/kmarket/product/view',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({"productId" : productId, "quantity": quantity}),
            success: function (data) {
                if (data.status === 200) {
                    window.location.href = '/kmarket/product/directOrder?productId=' + productId + '&quantity=' + encodeURIComponent(quantity);
                } else if (data.status === 400) {
                    alert(data.message);
                } else if (data.status === 600) {
                    window.location.href = '/kmarket/member/login';
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                alert("잘못된 요청입니다. 다시 실행해주세요.");
            }
        });
    });

    /////////// 댓글 페이지 자동 스크롤 ///////////

    let urlParams = new URLSearchParams(window.location.search); // 현재 페이지 URL 에서 쿼리 파라미터 가져오기
    let pageParam = urlParams.get('page');

    // 만약 "page" 파라미터가 존재하면 스크롤 이동
    if (pageParam) {
        // 여기에 스크롤 이동을 원하는 요소의 ID나 클래스 등을 지정
        let targetElement = document.getElementById('review-article');
        if (targetElement) {
            // 지정한 요소로 스크롤 이동
            targetElement.scrollIntoView({behavior: 'smooth', block: 'start'});
        }
    }
});

// 상품 더보기, 접기
function toggleProductInfo() {
    let productImageContainer = document.getElementById('productImageContainer');
    let toggleButton = document.getElementById('toggleButton');

    if (productImageContainer.classList.contains('fullImage')) {
        // 이미지가 전체 보이는 상태일 때
        productImageContainer.classList.remove('fullImage');
        toggleButton.textContent = '상품 정보 더보기';
    } else {
        // 이미지가 부분만 보이는 상태일 때
        productImageContainer.classList.add('fullImage');
        toggleButton.textContent = '상품 정보 접기';
    }
}

// 상품평보기 클릭 시 상품평으로 이동
$(document).ready(function() {
    // 이전 버튼 클릭 시
    $("#watchReview").click(function(e) {
        e.preventDefault(); // 기본 동작을 중단
        // 이동하고자 하는 요소로 스크롤 이동
        $("html, body").animate({
            scrollTop: $("#review-article").offset().top
        }, 500); // 이동에 걸리는 시간 (500ms)
    });
});




