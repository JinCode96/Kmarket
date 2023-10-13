window.onload = function() {
    const errorColor = "rgb(240,13,56)";
    const successColor = "rgb(13,136,241)";
    const basicColor = "rgb(174,174,174)";

    let loginIdOk = false;
    let passwordOk = false;
    let passwordConfirmOk = false;
    let companyOk = false;
    let ceoOk = false;
    let bizNumOk = false;
    let reportNumberOk = false;
    let numberOk = false;
    let addressOk = false;

    // 추가
    let bizNum1Ok = false;
    let bizNum2Ok = false;
    let bizNum3Ok = false;

    // input
    const loginIdInput = document.getElementById('loginId');
    const passwordInput = document.getElementById('password');
    const confirmPassInput = document.getElementById('confirmPass');
    const companyInput = document.getElementById('company');
    const ceoInput = document.getElementById('ceo');
    const bizNum1Input = document.getElementById('bizNum1');
    const bizNum2Input = document.getElementById('bizNum2');
    const bizNum3Input = document.getElementById('bizNum3');
    const reportNumberInput = document.getElementById('reportNumber');
    const areaNumberSelect = document.getElementById('areaNumber');
    const middleNumberInput = document.getElementById('middleNumber');
    const lastNumberInput = document.getElementById('lastNumber');
    const zipCodeInput = document.getElementById('zipCode');
    const addressInput = document.getElementById('address');
    const detailAddressInput = document.getElementById('detailAddress');
    const sellerForm = document.getElementById('sellerForm');

    // 정규식
    const loginIdRegEx = /^[a-z][a-z0-9]{5,12}$/; // 영문 소문자로 시작, 영문 + 숫자 6~13자리 조합
    const passwordRegEx = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\W)(?=\S+$).{7,15}$/; // 적어도 하나의 영문, 숫자, 특수문자 8~16자리 조합
    const ceoRegEx = /^(?:[가-힣\s]{2,20}|[a-zA-Z\s]{2,20})$/; // 한글 or 영문, 띄어쓰기 가능, 혼용 불가능 2~20자
    const reportNumberRegEx = /^(20\d{2}|19\d{2})-[가-힣]{2,7}-\d{4,5}$/;

    // result
    const loginIdResult = document.querySelector('.loginIdResult');
    const passwordResult = document.querySelector('.passwordResult');
    const confirmPassResult = document.querySelector('.confirmPassResult');
    const companyResult = document.querySelector('.companyResult');
    const ceoResult = document.querySelector('.ceoResult');
    const bizNumResult = document.querySelector('.bizNumResult');
    const reportNumberResult = document.querySelector('.reportNumberResult');
    const numberResult = document.querySelector('.numberResult');
    const addressResult = document.querySelector('.addressResult');

    // 아이디 유효성 검증
    loginIdInput.addEventListener('focusout', function () {
        let loginId = this.value;
        if (loginId === "") { // 공백
            displayResult(loginIdResult,"필수 정보입니다.", errorColor);
            loginIdOk = false;
        }else if (!loginIdRegEx.test(loginId)) { // 정규식 만족
            displayResult(loginIdResult, "회원 아이디(ID)는 띄어쓰기 없이 6~13자리의 영문자와 숫자 조합만 가능합니다", errorColor);
            loginIdOk = false;
        } else {
            checkSellerId(loginId);
        }
    });

    // 비밀번호 유효성 검증
    passwordInput.addEventListener('focusout', function () {
        let password = this.value;
        let confirmPass = confirmPassInput.value;
        if (password === "") {
            displayResult(passwordResult, "띄어쓰기 없는 8~16자의 대/소문자, 숫자, 특수문자 조합으로 입력하셔야 합니다.", errorColor);
            passwordOk = false;
        } else if (!passwordRegEx.test(password)) {
            displayResult(passwordResult, "비밀번호 조합 기준에 적합하지 않습니다.", errorColor);
            passwordOk = false;
        } else {
            displayResult(passwordResult, "적절한 비밀번호 입니다.", successColor);
            passwordOk = true;
        }
        if (confirmPass === "") {
            return;
        }
        if (password !== confirmPass) {
            displayResult(confirmPassResult, "비밀번호가 일치하지 않습니다.", errorColor);
            passwordConfirmOk = false;
        } else {
            displayResult(confirmPassResult, "비밀번호가 일치합니다.", successColor);
            passwordConfirmOk = true;
        }
    });

    // 비밀번호 확인
    confirmPassInput.addEventListener('focusout', function () {
        let confirmPass = this.value;
        let password = passwordInput.value;
        if (confirmPass === "") {
            displayResult(confirmPassResult, "비밀번호 확인이 필요합니다.", errorColor);
            passwordConfirmOk = false;
        } else if (confirmPass !== password) {
            displayResult(confirmPassResult, "비밀번호가 일치하지 않습니다.", errorColor);
            passwordConfirmOk = false;
        } else {
            displayResult(confirmPassResult, "비밀번호가 일치합니다.", successColor);
            passwordConfirmOk = true;
        }
    });

    // 회사명
    companyInput.addEventListener('focusout', function () {
        let company = this.value;
        if (company === "") {
            displayResult(companyResult, "필수 정보입니다.", errorColor);
            companyOk = false;
        } else {
            companyResult.innerText = "";
            companyOk = true;
        }
    });

    // 대표자 이름
    ceoInput.addEventListener('focusout', function () {
        let ceo = this.value;
        if (ceo === "") {
            displayResult(ceoResult, "필수 정보입니다.", errorColor);
            ceoOk = false;
        } else if (!ceoRegEx.test(ceo)) {
            displayResult(ceoResult, "이름 형식에 맞지 않습니다.", errorColor);
            ceoOk = false;
        } else {
            ceoResult.innerText = "";
            ceoOk = true;
        }
    });

    // 사업자 등록 번호 //
    bizNum1Input.addEventListener('focusout', function () {
        let bizNum1 = this.value;
        if (bizNum1 === "" || bizNum1.length !== 3) {
            displayResult(bizNumResult, "사업자 등록번호 앞자리는 3자입니다. 다시 입력해주세요.", errorColor);
            bizNum2Input.disabled = true;
            bizNum3Input.disabled = true;
            bizNum1Ok = false;
            bizNumOk = false;
        }
    });

    bizNum2Input.addEventListener('focusout', function () {
        let bizNum2 = this.value;
        if (bizNum2 === "" || bizNum2.length !== 2) {
            displayResult(bizNumResult, "사업자 등록번호 뒷자리는 2자입니다. 다시 입력해주세요.");
            bizNum1Input.disabled = true;
            bizNum3Input.disabled = true;
            bizNum2Ok = false;
            bizNumOk = false;
        }
    });

    bizNum3Input.addEventListener('focusout', function () {
        let bizNum3 = this.value;
        if (bizNum3 === "" || bizNum3.length !== 5) {
            displayResult(bizNumResult, "사업자 등록번호 뒷자리는 5자입니다. 다시 입력해주세요.", errorColor);
            bizNum1Input.disabled = true;
            bizNum2Input.disabled = true;
            bizNum3Ok = false;
            bizNumOk = false;
        }
    });

    bizNum1Input.addEventListener('input', function () {
        let bizNum1 = this.value;
        if (bizNum1.length === 3) {
            bizNum2Input.disabled = false;
            bizNum3Input.disabled = false;
            bizNumResult.innerText = "";
            bizNum1Ok = true;
            bizNum2Input.focus();
            bizNumOkValidation();
        }
    });

    bizNum2Input.addEventListener('input', function () {
        let bizNum2 = this.value;
        if (bizNum2.length === 2) {
            bizNum1Input.disabled = false;
            bizNum3Input.disabled = false;
            bizNumResult.innerText = "";
            bizNum2Ok = true;
            bizNum3Input.focus();
            bizNumOkValidation();
        }
    });

    bizNum3Input.addEventListener('input', function () {
        let bizNum3 = this.value;
        if (bizNum3.length === 5) {
            bizNum1Input.disabled = false;
            bizNum2Input.disabled = false;
            bizNumResult.innerText = "";
            bizNum3Ok = true;
            bizNumOkValidation()
        }
    });

    // 통신판매업신고 번호
    reportNumberInput.addEventListener('focusout', function () {
        let reportNumber = this.value;
        if (reportNumber === "" || !reportNumberRegEx.test(reportNumber)) {
            displayResult(reportNumberResult, "번호를 정확하게 입력해주세요. \"년도-지역-일련번호\" 형식", errorColor);
            reportNumberOk = false;
        } else {
            displayResult(reportNumberResult, "\"년도-지역-4자리번호\" 형식, 예) 2017-경기성남-0011", basicColor);
            reportNumberOk = true;
        }
    });

    // 전화번호 유효성 검사
    middleNumberInput.addEventListener('focusout', validateNumber);
    lastNumberInput.addEventListener('focusout', validateNumber);
    middleNumberInput.addEventListener('input', function () {
        let middleNumber = this.value;
        if (middleNumber.length === 4) {
            lastNumberInput.focus();
        }
    });

    // 상세 주소
    detailAddressInput.addEventListener('focusout', function () {
        let zipCode = zipCodeInput.value;
        let address = addressInput.value;
        let detailAddress = this.value;
        if (zipCode === "" || address === "" || detailAddress === "") {
            displayResult(addressResult, "주소를 입력해 주세요.", errorColor);
        } else {
            addressResult.innerText = "";
            addressOk = true;
        }
    });

    // submit
    sellerForm.addEventListener('submit', function (event) {
        event.preventDefault();
        if (!validateForm()) {
            return;
        }
        const member = gatherMemberData();
        sendMemberToServer(member);
    });

    ///////////////// ajax /////////////////

    // 아이디 중복 ajax
    function checkSellerId(loginId) {
        $.ajax({
            url: "/kmarket/member/register/checkLoginId/seller",
            method: "POST",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({"loginId": loginId}),
            success: function (data) {
                if (data.status === 1) {
                    displayResult(loginIdResult, data.message, errorColor);
                    loginIdOk = false;
                } else if (data.status === 0) {
                    displayResult(loginIdResult, data.message, successColor);
                    loginIdOk = true;
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error(xhr.responseText);
                alert("잘못된 요청입니다. 다시 실행해주세요.");
                loginIdOk = false;
            }
        });
    }

    // 최종 회원가입
    function sendMemberToServer(member) {
        $.ajax({
            url: "/kmarket/member/register/seller",
            method: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(member),
            success: function (data) {
                if (data.status === 1) {
                    alert(data.message);
                    window.location.href = "/kmarket/member/login";
                } else {
                    alert(data.message);
                    window.location.href = "/kmarket/member/join";
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error(xhr.responseText);
                alert("잘못된 요청입니다. 다시 실행해주세요.");
            }
        });
    }

    ///////////////// function /////////////////

    function gatherMemberData() {
        const loginId = loginIdInput.value;
        const password = passwordInput.value;
        const company = companyInput.value;
        const ceo = ceoInput.value;
        const businessRegistrationNumber = bizNum1Input.value + "-" + bizNum2Input.value + "-" + bizNum3Input.value;
        const reportNumber = reportNumberInput.value;
        const phoneNumber = areaNumberSelect.value + "-" + middleNumberInput.value + "-" + lastNumberInput.value;
        const zipCode = zipCodeInput.value;
        const address = addressInput.value;
        const detailAddress = detailAddressInput.value;

        return {
            loginId,
            password,
            company,
            ceo,
            businessRegistrationNumber,
            reportNumber,
            phoneNumber,
            zipCode,
            address,
            detailAddress
        };
    }

    // submit 최종 검증 후 alert, focus
    function validateForm() {
        const validations = [
            { isValid: loginIdOk, message: '아이디를 확인해주세요.', input: loginIdInput },
            { isValid: passwordOk, message: '비밀번호를 확인해주세요.', input: passwordInput },
            { isValid: passwordConfirmOk, message: '비밀번호가 틀립니다.', input: confirmPassInput },
            { isValid: companyOk, message: '회사명을 확인해주세요.', input: companyInput },
            { isValid: ceoOk, message: '대표자 이름을 확인해주세요.', input: ceoInput },
            { isValid: bizNumOk, message: '사업자등록번호를 확인해주세요.', input: bizNum1Input },
            { isValid: reportNumberOk, message: '통신판매업신고 번호를 확인해주세요.', input: reportNumberInput },
            { isValid: numberOk, message: '휴대폰 번호를 확인해주세요.', input: middleNumberInput },
            { isValid: addressOk, message: '주소를 확인해주세요.' }
        ];
        for (const validation of validations) {
            if (!validation.isValid) {
                alert(validation.message);
                if (validation.input) {
                    validation.input.focus();
                }
                return false;
            }
        }
        return true;
    }

    // 전화번호 유효성 검사 함수
    function validateNumber() {
        let middleNumber = middleNumberInput.value;
        let lastNumber = lastNumberInput.value;
        if (middleNumber === "" || lastNumber === "") {
            displayResult(numberResult, "전화번호를 정확히 입력해 주세요.", errorColor);
            numberOk = false;
        } else {
            displayResult(numberResult, "", "");
            numberOk = true;
        }
    }

    // 사업자등록번호 검증
    function bizNumOkValidation() {
        if (bizNum1Ok && bizNum2Ok && bizNum3Ok) {
            bizNumOk = true;
        } else {
            bizNumOk = false;
        }
    }

    function displayResult(element, message, color) {
        element.innerText = "\u00a0" + message;
        element.style.color = color;
    }

    // 주소 입력 빼고 띄어쓰기 방지
    const excludedIds = ['company', 'ceo', 'reportNumber', 'detailAddress'];
    document.querySelectorAll("input[type='text'], input[type='password']").forEach(input => {
        if (!excludedIds.includes(input.id)) {
            input.addEventListener("input", function () {
                this.value = this.value.replace(/\s/g, "");
            });
        }
    });
}