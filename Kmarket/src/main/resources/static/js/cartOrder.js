/**
 * 바로 주문 유효성 검사, 결제
 */
document.addEventListener('DOMContentLoaded', function () {

    const inputs = {
        // productId: document.getElementById('productId'), // productId
        productId : document.querySelectorAll('.productId'),
        quantity: document.querySelectorAll('.quantity'),
        receiptName : document.getElementById('receiptName'), // 수령인 이름
        receiptHp : document.getElementById('receiptHp'), // 수령인 전화번호
        receiptZip : document.getElementById('zipCode'), // 수령인 우편번호
        receiptAddress : document.getElementById('address'), // 수령인 주소
        receiptDetailAddress : document.getElementById('detailAddress'), // 수령인 상세 주소
        usedPoint : document.getElementById('usedPoint'), // 사용할 포인트
        creditCard: document.getElementById('creditCard'),
        checkCard: document.getElementById('checkCard'),
        accountTransfer: document.getElementById('accountTransfer'),
        bankTransfer: document.getElementById('bankTransfer'),
        mobilePayment: document.getElementById('mobilePayment'),
        kakaoPay : document.getElementById('kakaoPay') // 카카오페이
    };

    const paymentBt = document.getElementById('payment'); // 결제하기 버튼

    const regexName = /^(?:[가-힣\s]{2,20}|[a-zA-Z\s]{2,20})$/;
    const regexHp = /^(02|031|032|033|041|042|043|044|051|052|053|054|055|061|062|063|064|010)-\d{4}-\d{4}$/;

    /**
     * 결제하기 버튼
     */
    paymentBt.addEventListener('click', function () {

        let receiptName = inputs.receiptName.value;
        let receiptHp = inputs.receiptHp.value;
        let receiptZip = inputs.receiptZip.value;
        let receiptAddress = inputs.receiptAddress.value;
        let receiptDetailAddress = inputs.receiptDetailAddress.value;
        let selectedPayment = document.querySelector('input[name="payment"]:checked'); // 선택된 라디오 버튼의 ID 가져오기
        let productIds = Array.from(inputs.productId).map(input => input.value);
        let quantities = Array.from(inputs.quantity).map(input => input.value);
        let usedPoint = parseInt(inputs.usedPoint.value.replace(/,/g, ''));

        // 리스트 객체로 변환
        let idAndQuantities = [];
        for (let i = 0; i < productIds.length; i++) {
            let idAndQuantity = {
                productId: parseInt(productIds[i]),
                quantity: parseInt(quantities[i])
            };
            idAndQuantities.push(idAndQuantity);
        }

        if (receiptName === '' || !regexName.test(receiptName)) {
            alert('주문자를 정확하게 입력해주세요.');
            return;
        }
        if (receiptHp === '' || !regexHp.test(receiptHp)) {
            alert('휴대폰 번호를 정확하게 입력해주세요.');
            return;
        }
        if (receiptZip === '' || receiptAddress === '' || receiptDetailAddress === '') {
            alert('주소를 정확하게 입력해주세요.');
            return;
        }

        if (usedPoint === null) {
            usedPoint = 0;
        }

        // 결제 방법에 따른 처리
        if (!selectedPayment){
            alert('결제 방법을 선택해주세요.');
        } else if (selectedPayment && selectedPayment.id === 'kakaoPay') {
            // 카카오페이 ajax
            $.ajax({
                url: "/kmarket/product/cartOrderPay",
                method: 'POST',
                contentType: "application/json",
                dataType: 'json',
                data: JSON.stringify({"receiptName" : receiptName, "receiptHp" : receiptHp, "receiptZip" : receiptZip, "receiptAddress" : receiptAddress, "receiptDetailAddress" : receiptDetailAddress,
                    "idAndQuantities" : idAndQuantities, "usedPoint" : usedPoint}),
                success: function (data) {
                    // alert('완료');
                    location.href = data.next_redirect_pc_url; // 카카오페이 결제 화면으로 이동
                },
                error: function () {
                    alert("잘못된 요청입니다. 다시 실행해주세요.");
                }
            });
        } else {
            alert('준비 중입니다.');
        }
    });
});