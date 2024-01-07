/**
 * 포인트 사용 코드
 */
document.addEventListener('DOMContentLoaded', function () {

    const productPriceTd = document.getElementById('productPrice'); // 상품 금액
    const memberPointSpan = document.getElementById('memberPoint'); // 회원이 가지고 있는 포인트
    const usedPointInput = document.getElementById('usedPoint'); // 사용할 포인트
    const allPointCheck = document.getElementById('allPointCheck'); // 최대 포인트 사용 체크박스
    const pointDiscountInput = document.getElementById('pointDiscount'); // 적용된 포인트
    const totalPriceTd = document.getElementById('totalPrice'); // 총 결제 금액

    if (parseInt(memberPointSpan.getAttribute('memberPoint')) < 5000) { // 회원 포인트가 5000 미만이면 포인트 사용 불가
        usedPointInput.disabled = true;
        allPointCheck.disabled = true;
    }

    // 숫자만 입력 가능하도록
    usedPointInput.addEventListener('input', function () {
        allowOnlyNumbers(usedPointInput);
    });


    usedPointInput.addEventListener('input', function () {
        const productPrice = parseInt(productPriceTd.getAttribute('productPrice')); // 상품 금액
        const memberPoint = parseInt(memberPointSpan.getAttribute('memberPoint')); // 회원 포인트
        const usedPoint = parseInt(usedPointInput.value.replace(/,/g, '')); // 사용할 포인트 (콤마 빼기)
        const totalPrice = parseInt(totalPriceTd.getAttribute('totalPrice')); // 총 결제 금액

        // console.log('memberPoint = ' + memberPoint + ' usedPoint = ' + usedPoint + ' totalPrice = ' + totalPrice);
        if (isNaN(usedPoint)) {
            usedPointInput.value = 0;
            pointDiscountInput.textContent = parseInt(usedPointInput.value) + '원';
            totalPriceTd.textContent = addCommas(totalPrice - parseInt(usedPointInput.value)) + '원';
        } else if (usedPoint > memberPoint) {
            alert('보유하신 포인트보다 초과하였습니다.\n현재 포인트 : ' + addCommas(memberPoint) + 'P');
            if (memberPoint > productPrice) {
                usedPointInput.value = addCommas(productPrice);
                pointDiscountInput.textContent = '-' + addCommas(parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
                totalPriceTd.textContent = addCommas(totalPrice - parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
            } else {
                usedPointInput.value = addCommas(memberPoint); // 가지고 있는 최대 포인트 기입
                pointDiscountInput.textContent = '-' + addCommas(parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
                totalPriceTd.textContent = addCommas(totalPrice - parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
            }
        } else if (usedPoint > productPrice) {
            alert('포인트 사용액이 결제 금액보다 많습니다.');
            usedPointInput.value = 0;
            pointDiscountInput.textContent = parseInt(usedPointInput.value) + '원';
            totalPriceTd.textContent = addCommas(totalPrice - parseInt(usedPointInput.value)) + '원';
        } else {
            // 포인트 적용
            pointDiscountInput.textContent = '-' + addCommas(usedPoint) + '원'; // 콤마 추가
            totalPriceTd.textContent = addCommas(totalPrice - usedPoint) + '원'; // 총 결제금액 업데이트
        }
    });

    // 최대 포인트 사용 체크
    allPointCheck.addEventListener('change', function () {
        // alert('hi');
        const memberPoint = parseInt(memberPointSpan.getAttribute('memberPoint')); // 회원 포인트
        const productPrice = parseInt(productPriceTd.getAttribute('productPrice')); // 상품 금액
        const totalPrice = parseInt(totalPriceTd.getAttribute('totalPrice')); // 총 결제 금액

        if (allPointCheck.checked) {
            if (memberPoint > productPrice) {
                usedPointInput.value = addCommas(productPrice);
                pointDiscountInput.textContent = '-' + addCommas(parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
                totalPriceTd.textContent = addCommas(totalPrice - parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
            } else {
                usedPointInput.value = addCommas(memberPoint);
                pointDiscountInput.textContent = '-' + addCommas(parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
                totalPriceTd.textContent = addCommas(totalPrice - parseInt(usedPointInput.value.replace(/,/g, ''))) + '원';
            }
        } else {
            usedPointInput.value = 0;
            pointDiscountInput.textContent = parseInt(usedPointInput.value) + '원';
            totalPriceTd.textContent = addCommas(totalPrice - parseInt(usedPointInput.value)) + '원';
        }
    });

    // 숫자에 콤마 추가 함수
    function addCommas(number) {
        return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    // 숫자만, 앞자리 0 없애기
    function allowOnlyNumbers(inputElement) {
        // 입력된 값에서 숫자만 추출하여 적용
        inputElement.value = addCommas(inputElement.value.replace(/[^0-9]/g, ''));

        // 제일 첫 자리에 0이 오고 뒤에 다른 숫자가 오면 0을 지우도록 처리
        if (inputElement.value.length > 1 && inputElement.value[0] === '0') {
            inputElement.value = inputElement.value.slice(1);
        }
    }
});






