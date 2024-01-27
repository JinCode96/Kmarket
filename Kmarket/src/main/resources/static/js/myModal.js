let productId;
let productName;

const reviewButton = document.querySelectorAll('.reviewWrite');
reviewButton.forEach(button => {
    button.addEventListener('click', function () {

        const modal = document.getElementById('myModal');
        const productNameModal = document.getElementById('productNameModal');
        const currentProductId = button.getAttribute('productId'); // productId 가져오기
        const currentProductName = button.getAttribute('productName');
        productId = currentProductId;
        productName = currentProductName;

        productNameModal.innerText = productName; // 상품명

        // 모달창 띄우기
        modal.style.display = 'block';
    });
});

// 모달창 닫기
function closeReviewModal() {
    const modal = document.getElementById('myModal');
    modal.style.display = 'none';
    textAreaReset();
}

// 상품평 적용 버튼 클릭 시 동작
function submitReview() {
    const content = document.getElementById('reviewText').value.trim();

    if (content === "") {
        alert('상품평을 입력해주세요.');
    } else {
        $.ajax({
            url: '/kmarket/my/saveReview',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({"productId": productId, "content" : content}),
            success: function (data) {
                if (data.status === 200) {
                    alert("리뷰가 등록되었습니다.");
                    closeReviewModal();
                    textAreaReset();
                } else if (data.status === 400) {
                    alert("전송 중 문제가 발생하였습니다. 다시 시도해주세요.");
                }
            }
        });
    }
}

// 모달 외부를 클릭하면 모달창 닫기 (선택적으로)
window.onclick = function(event) {
    const modal = document.getElementById('myModal');
    if (event.target == modal) {
        modal.style.display = 'none';
        textAreaReset();
    }
}

function textAreaReset() {
    document.getElementById('reviewText').value = '';
}