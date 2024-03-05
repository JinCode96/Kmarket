const deleteProduct = document.querySelectorAll('.deleteProduct');

/**
 * 상품 단일 삭제
 */
deleteProduct.forEach(button => {
    button.addEventListener('click', function (event) {
        event.preventDefault(); // 링크 기본동작 방지
        const productId = button.getAttribute('data-product-id');
        const productName = button.getAttribute('data-product-productName');

        if (confirm("상품코드 : " + productId + "\n" + "상품명 : " + productName + "\n" + "해당 상품을 삭제하시겠습니까?")) {

            /**
             * 상품 단일 삭제
             */
            $.ajax({
                url: '/kmarket/admin/products',
                method: 'DELETE',
                dataType: 'json',
                data: {"productId": productId},
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
    });
});

/**
 * 체크박스 선택 삭제
 */
function deleteSelectedProducts() {
    const selectedProducts  = document.querySelectorAll('input[name="productCode"]:checked');
    let productIds = Array.from(selectedProducts).map(function (checkbox) {
        return checkbox.value;
    });

    if (productIds.length > 0) {
        if (confirm(productIds.length + "개의 상품을 삭제하시겠습니까?")) {

            /**
             * 상품 다중 삭제
             */
            $.ajax({
                url: '/kmarket/admin/selectedProducts',
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

/**
 * 검색어 validation
 */
function searchValidate() {
    const searchField = document.getElementsByName("searchField")[0].value;
    const keyword = document.getElementsByName("keyword")[0].value;
    if (keyword.trim() === "") {
        alert("검색어를 입력하세요");
        return false;
    }
    if (searchField === "id" && !/^\d+$/.test(keyword)) {
        alert("상품코드에는 숫자만 입력 가능합니다");
        return false;
    }
    return true;
}
