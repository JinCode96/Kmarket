// document.addEventListener('DOMContentLoaded', function () {
//
//     const orderBt = document.getElementById('order');
//
//     orderBt.addEventListener('click', function () {
//         alert('hi');
//
//         // th:attr 로 지정한 'carts' 속성을 가져와서 JSON 문자열을 객체로 파싱
//         const carts = JSON.parse(orderBt.getAttribute('carts'));
//         console.log('carts = ' + carts);
//     });
// });

/**
 * 주문하기 페이지 GET
 */
function order() {
    const productIdInput = document.querySelectorAll('.productId');

    let productIds = Array.from(productIdInput).map(input => input.value);
    console.log('productIds = ' + productIds);

    // 배열을 쉼표로 연결하여 URL 에 전달
    const queryString = 'productIds=' + encodeURIComponent(productIds.join(','));

    console.log('queryString = ' + queryString);

    // window.location.href를 사용하여 페이지 이동
    window.location.href = '/kmarket/product/cartOrder?' + queryString;
}

function adjustQuantity(button, action) {
    let productId = button.parentNode.querySelector('.hiddenProductId').value;
    let quantityInput = button.parentNode.querySelector('input[name="num"]');
    let currentQuantity = parseInt(quantityInput.value);

    if (action === 'decrease') {
        if (currentQuantity - 1 < 1) {
            alert('주문 가능한 최소 수량은 1개 입니다.');
            ajaxChangeQuantity(JSON.stringify({"productId": productId, "quantity" : 1, "action": 'input'}));
            return;
        }
    }
    if (action === 'increase') {
        if (currentQuantity + 1 > 100) {
            alert('주문 가능한 최대 수량은 100개 입니다.');
            ajaxChangeQuantity(JSON.stringify({"productId": productId, "quantity" : 100, "action": 'input'}));
            return;
        }
    }

    if (currentQuantity < 1) {
        alert('주문 가능한 최소 수량은 1개 입니다.');
        ajaxChangeQuantity(JSON.stringify({"productId": productId, "quantity" : 1, "action": action}));
    } else if (currentQuantity > 100) {
        alert('주문 가능한 최대 수량은 100개 입니다.');
        ajaxChangeQuantity(JSON.stringify({"productId": productId, "quantity" : 100, "action": action}));
    } else {
        ajaxChangeQuantity(JSON.stringify({"productId": productId, "quantity" : currentQuantity, "action" : action}));
    }
}

function ajaxChangeQuantity(object) {

    /**
     * 상품 수량 변경
     */
    $.ajax({
        url: '/kmarket/product/changeQuantity',
        method: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        data: object,
        success: function (data) {
            if (data.status === 200) {
                location.reload();
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            alert("잘못된 요청입니다. 다시 실행해주세요.");
        }
    });
}

/**
 * 장바구니 상품 삭제
 */
function deleteCart() {
    const selectedBoxes = document.querySelectorAll('input[name="cartCheckBox"]:checked');

    let productIds = Array.from(selectedBoxes).map(function (checkBox) {
        return checkBox.value;
    });

    if (productIds.length > 0) {

        if (confirm(productIds.length + "개의 상품을 삭제하시겠습니까?")) {

            /**
             * 상품 삭제
             */
            $.ajax({
                url: '/kmarket/product/deleteCart',
                method: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify({"productIds": productIds}),
                success: function (data) {
                    if (data.status === 200) {
                        alert(data.message);
                        location.reload();
                    } else if (data.status === 400) {
                        alert(data.message);
                        location.reload();
                    }
                }
            });
        }

    } else {
        alert('삭제할 상품을 선택해주세요.');
    }
}