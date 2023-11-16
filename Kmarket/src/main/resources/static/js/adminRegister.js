function updateCategory2Options(selectElement) {
    let selectedCategory1 = selectElement.value;
    let category2Select = document.getElementById('category2Select');

    // 2차 분류 선택 목록 초기화
    category2Select.innerHTML = '<option value="" disabled selected>2차 분류 선택</option>';

    // 선택한 1차 분류에 따라 동적으로 2차 분류 옵션 추가
    switch (selectedCategory1) {
        case '10':
            addOptionsToCategory2(category2Select, ['브랜드 여성의류', '브랜드 남성의류', '브랜드 진/캐쥬얼', '브랜드 신발/가방', '브랜드 쥬얼리/시계', '브랜드 아웃도어']);
            break;
        case '11':
            addOptionsToCategory2(category2Select,['여성의류', '남성의류', '언더웨어', '신발', '가방/잡화', '쥬얼리/시계', '화장품/향수', '바디/헤어']);
            break;
        case '12':
            addOptionsToCategory2(category2Select,['출산/육아', '장난감/완구', '유아동 의류', '유아동 신발/잡화']);
            break;
        case '13':
            addOptionsToCategory2(category2Select,['신선식품', '가공식품', '건강식품', '커피/음료', '생필품', '바디/헤어']);
            break;
        case '14':
            addOptionsToCategory2(category2Select,['가구/DIY', '침구/커튼', '조명/인테리어', '생활용품', '주방용품', '문구/사무용품', '사무기기', '악기/취미', '반려동물용품']);
            break;
        case '15':
            addOptionsToCategory2(category2Select,['노트북/PC', '모니터/프린터', 'PC주변기기', '모바일/태블릿', '카메라', '게임', '영상가전', '주방가전', '계절가전', '생활/미용가전', '음향가전', '건강가전']);
            break;
        case '16':
            addOptionsToCategory2(category2Select,['스포츠의류/운동화', '휘트니스/수영', '구기/라켓', '골프', '자전거/보드/기타레저', '캠핑/낚시', '등산/아웃도어', '건강/의료용품', '건강식품', '렌탈서비스']);
            break;
        case '17':
            addOptionsToCategory2(category2Select,['자동차용품', '공구/안전/산업용품']);
            break;
        case '18':
            addOptionsToCategory2(category2Select,['여행/항공권', '도서/음반/e교육', '공연티켓', 'e쿠폰', '상품권']);
            break;
        default:
            break;
    }
}

// 2차 분류 옵션을 추가하는 함수
function addOptionsToCategory2(selectElement, options) {
    for (let i = 0; i < options.length; i++) {
        let option = document.createElement('option');
        option.value = [i+10];
        option.text = options[i];
        selectElement.appendChild(option);
    }
}
