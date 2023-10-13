/*** 회원 등록 js ***/
window.onload = function() {

    // 폰트 색 설정
    const errorColor = "rgb(240,13,56)";
    const successColor = "rgb(13,136,241)";

    // validation check
    let loginIdOk = false;
    let passwordOk = false;
    let passwordConfirmOk = false;
    let nameOk = false;
    let emailOk = false;
    let numberOk = false;
    let addressOk = false;

    // Input 변수 선언
    const loginIdInput = document.getElementById('loginId');
    const passwordInput = document.getElementById('password');
    const confirmPassInput = document.getElementById('confirmPass');
    const nameInput = document.getElementById('name');
    const emailPrefixInput = document.getElementById('emailPrefix');
    const emailDomainInput = document.getElementById('emailDomain');
    const emailDomainSelect = document.getElementById('domainSelect');
    const areaNumberSelect = document.getElementById('areaNumber');
    const middleNumberInput = document.getElementById('middleNumber');
    const lastNumberInput = document.getElementById('lastNumber');
    const zipCodeInput = document.getElementById('zipCode');
    const addressInput = document.getElementById('address');
    const detailAddressInput = document.getElementById('detailAddress');
    const generalForm = document.getElementById('generalForm');


    // 정규식 선언
    const loginIdRegEx = /^[a-z][a-z0-9]{5,12}$/; // 영문 소문자로 시작, 영문 + 숫자 6~13자리 조합
    const passwordRegEx = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\W)(?=\S+$).{7,15}$/; // 적어도 하나의 영문, 숫자, 특수문자 8~16자리 조합
    const nameRegEx = /^(?:[가-힣\s]{2,20}|[a-zA-Z\s]{2,20})$/; // 한글 or 영문, 띄어쓰기 가능, 혼용 불가능 2~20자
    const emailRegEx = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i; // 이메일 정규식

    // result
    const loginIdResult = document.querySelector('.loginIdResult');
    const passwordResult = document.querySelector('.passwordResult');
    const confirmPassResult = document.querySelector('.confirmPassResult');
    const nameResult = document.querySelector('.nameResult');
    const emailResult = document.querySelector('.emailResult');
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
            checkGeneralId(loginId); // ajax
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

    // 이름 유효성 검사
    nameInput.addEventListener('focusout', function () {
        let name = this.value;
        if (name === "") {
            displayResult(nameResult, "필수 정보입니다.", errorColor);
            nameOk = false;
        } else if (!nameRegEx.test(name)) {
            displayResult(nameResult, "이름 형식에 맞지 않습니다.", errorColor);
            nameOk = false;
        } else {
            nameResult.innerText = "";
            nameOk = true;
        }
    });

    // 이메일 유효성 검사
    emailPrefixInput.addEventListener('focusout', handleEmailValidationAndCheck);
    emailDomainInput.addEventListener('focusout', handleEmailValidationAndCheck);
    emailDomainSelect.addEventListener('change', handleEmailValidationAndCheck);

    // 전화번호 유효성 검사
    middleNumberInput.addEventListener('focusout', validateNumber);
    lastNumberInput.addEventListener('focusout', validateNumber);

    // 상세 주소 유효성 검사
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
    generalForm.addEventListener('submit', function (event) {
        event.preventDefault(); // form 동작 막기
        if (!validateForm()) {
            return;
        }
        const member = gatherMemberData();
        sendMemberDataToServer(member);
    });

    ///////////////// ajax ///////////////////

    // 아이디 중복 체크 ajax
    function checkGeneralId(loginId) {
        $.ajax({
            url: "/kmarket/member/register/checkLoginId/general",
            method: "POST",
            contentType: "application/json", // 안 붙이면 "="이 자꾸 붙음..
            dataType: 'json', // 서버에서 json 응답 기대
            data: JSON.stringify({"loginId": loginId}), // 객체 형태로 전송
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

    // 이메일 중복 체크 ajax
    function checkEmail(email) {
        $.ajax({
            url: "/kmarket/member/register/checkEmail",
            method: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({"email" : email}),
            success: function (data) {
                if (data.status === 1) {
                    displayResult(emailResult, data.message, errorColor);
                    emailOk = false;
                } else if (data.status === 0) {
                    displayResult(emailResult, data.message, successColor);
                    emailOk = true;
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error(xhr.responseText);
                alert("잘못된 요청입니다. 다시 실행해주세요.");
                emailOk = false;
            }
        });
    }

    // member 데이터 ajax 통신
    function sendMemberDataToServer(member) {
        $.ajax({
            url: "/kmarket/member/register/general",
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

    ///////////////// function ///////////////////

    // member data 만들기
    function gatherMemberData() {
        const loginId = loginIdInput.value;
        const password = passwordInput.value;
        const name = nameInput.value;
        const email = emailPrefixInput.value + "@" + emailDomainInput.value;
        const phoneNumber = areaNumberSelect.value + "-" + middleNumberInput.value + "-" + lastNumberInput.value;
        const zipCode = zipCodeInput.value;
        const address = addressInput.value;
        const detailAddress = detailAddressInput.value;

        return {
            loginId,
            password,
            name,
            email,
            phoneNumber,
            zipCode,
            address,
            detailAddress
        };
    }

    // form 데이터 컨펌
    function validateForm() {
        const validations = [
            { isValid: loginIdOk, message: '아이디를 확인해주세요.', input: loginIdInput },
            { isValid: passwordOk, message: '비밀번호를 확인해주세요.', input: passwordInput },
            { isValid: passwordConfirmOk, message: '비밀번호가 틀립니다.', input: confirmPassInput },
            { isValid: nameOk, message: '이름을 확인해주세요.', input: nameInput },
            { isValid: emailOk, message: '이메일을 확인해주세요.', input: emailPrefixInput },
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

    // 이메일 검증 및 이메일 검사 함수 호출
    function handleEmailValidationAndCheck() {
        if (validateEmail()) {
            checkEmail(emailPrefixInput.value + "@" + emailDomainInput.value);
        } else {
            emailOk = false;
        }
    }

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

    function validateEmail() {
        const emailPrefix = emailPrefixInput.value;
        const emailDomain = emailDomainInput.value;
        const email = emailPrefix + "@" + emailDomain;

        if (emailPrefix === "" || emailDomain === "" || !emailRegEx.test(email)) {
            displayResult(emailResult, "이메일 주소를 다시 확인해주세요.", errorColor);
            return false;
        }
        return true;
    }

    function displayResult(element, message, color) {
        element.innerText = "\u00a0" + message;
        element.style.color = color;
    }

    // 주소, 이름 빼고 띄어쓰기 방지
    const excludedIds = ['detailAddress', 'name'];
    document.querySelectorAll("input[type='text'], input[type='password']").forEach(input => {
        if (!excludedIds.includes(input.id)) {
            input.addEventListener("input", function () {
                this.value = this.value.replace(/\s/g, "");
            });
        }
    });
}