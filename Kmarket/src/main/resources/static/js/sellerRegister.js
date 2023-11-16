window.onload = function() {

    const errorColor = "rgb(240,13,56)";
    const successColor = "rgb(13,136,241)";
    const basicColor = "rgb(174,174,174)";

    // validation check
    let formValid = false;
    let validation = {
        loginId: false,
        password: false,
        confirmPassword: false,
        name: false,
        company: false,
        ceo: false,
        bizNum: false,
        bizNum1: false,
        bizNum2: false,
        bizNum3: false,
        reportNumber: false,
        phoneNumber: false,
        address: false
    };

    const form = document.getElementById('sellerForm');
    const inputs = {
        loginId: document.getElementById('loginId'),
        password: document.getElementById('password'),
        confirmPass: document.getElementById('confirmPass'),
        name: document.getElementById('name'),
        company: document.getElementById('company'),
        ceo: document.getElementById('ceo'),
        bizNum1: document.getElementById('bizNum1'),
        bizNum2: document.getElementById('bizNum2'),
        bizNum3: document.getElementById('bizNum3'),
        reportNumber: document.getElementById('reportNumber'),
        areaNumber: document.getElementById('areaNumber'),
        middleNumber: document.getElementById('middleNumber'),
        lastNumber: document.getElementById('lastNumber'),
        zipCode: document.getElementById('zipCode'),
        address: document.getElementById('address'),
        detailAddress: document.getElementById('detailAddress')
    };

    const regex = {
        loginId: /^[a-z][a-z0-9]{5,12}$/,
        password: /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\W)(?=\S+$).{7,15}$/,
        name: /^(?:[가-힣\s]{2,20}|[a-zA-Z\s]{2,20})$/,
        ceo: /^(?:[가-힣\s]{2,20}|[a-zA-Z\s]{2,20})$/,
        reportNumber: /^(20\d{2}|19\d{2})-[가-힣]{2,7}-\d{4,5}$/
    };

    const results = {
        loginId: document.querySelector('.loginIdResult'),
        password: document.querySelector('.passwordResult'),
        confirmPass: document.querySelector('.confirmPassResult'),
        name: document.querySelector('.nameResult'),
        company: document.querySelector('.companyResult'),
        ceo: document.querySelector('.ceoResult'),
        bizNum: document.querySelector('.bizNumResult'),
        reportNumber: document.querySelector('.reportNumberResult'),
        phoneNumber: document.querySelector('.numberResult'),
        address: document.querySelector('.addressResult'),
    };

    inputs.loginId.addEventListener('focusout', validateLoginId);
    inputs.password.addEventListener('focusout', validatePassword);
    inputs.confirmPass.addEventListener('focusout', validateConfirmPassword);
    inputs.name.addEventListener('focusout', validateName);
    inputs.company.addEventListener('focusout', validateCompany);
    inputs.ceo.addEventListener('focusout', validateCeo);
    inputs.bizNum1.addEventListener('focusout', validateBizNum1);
    inputs.bizNum2.addEventListener('focusout', validateBizNum2);
    inputs.bizNum3.addEventListener('focusout', validateBizNum3);
    inputs.bizNum1.addEventListener('input', focusBizNum1);
    inputs.bizNum2.addEventListener('input', focusBizNum2);
    inputs.bizNum3.addEventListener('input', focusBizNum3);
    inputs.reportNumber.addEventListener('focusout', validateReportNumber);
    inputs.middleNumber.addEventListener('focusout', validatePhoneNumber);
    inputs.lastNumber.addEventListener('focusout', validatePhoneNumber);
    inputs.middleNumber.addEventListener('input', focusOnLastNumber);
    inputs.detailAddress.addEventListener('focusout', validationAddress);

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        formValid = validateForm();
        if (formValid) {
            const member = gatherMemberData();
            sendMemberToServer(member);
        }
    });

    ////////////////// function //////////////////

    function validateLoginId() {
        let loginId = inputs.loginId.value;
        if (loginId === "") { // 공백
            displayResult(results.loginId,"필수 정보입니다.", errorColor);
            validation.loginId = false;
        }else if (!regex.loginId.test(loginId)) { // 정규식 만족
            displayResult(results.loginId, "회원 아이디(ID)는 띄어쓰기 없이 6~13자리의 영문자와 숫자 조합만 가능합니다", errorColor);
            validation.loginId = false;
        } else {
            ajaxCheckSellerId(loginId);
        }
    }

    function validatePassword() {
        let password = inputs.password.value;
        let confirmPass = inputs.confirmPass.value;
        if (password === "") {
            displayResult(results.password, "띄어쓰기 없는 8~16자의 대/소문자, 숫자, 특수문자 조합으로 입력하셔야 합니다.", errorColor);
            validation.password = false;
        } else if (!regex.password.test(password)) {
            displayResult(results.password, "비밀번호 조합 기준에 적합하지 않습니다.", errorColor);
            validation.password = false;
        } else {
            displayResult(results.password, "적절한 비밀번호 입니다.", successColor);
            validation.password = true;
        }
        if (confirmPass === "") {
            return;
        }
        if (password !== confirmPass) {
            displayResult(results.confirmPass, "비밀번호가 일치하지 않습니다.", errorColor);
            validation.confirmPassword = false;
        } else {
            displayResult(results.confirmPass, "비밀번호가 일치합니다.", successColor);
            validation.confirmPassword = true;
        }
    }

    function validateConfirmPassword() {
        let confirmPass = inputs.confirmPass.value;
        let password = inputs.password.value;
        if (confirmPass === "") {
            displayResult(results.confirmPass, "비밀번호 확인이 필요합니다.", errorColor);
            validation.confirmPassword = false;
        } else if (confirmPass !== password) {
            displayResult(results.confirmPass, "비밀번호가 일치하지 않습니다.", errorColor);
            validation.confirmPassword = false;
        } else {
            displayResult(results.confirmPass, "비밀번호가 일치합니다.", successColor);
            validation.confirmPassword = true;
        }
    }

    function validateName() {
        let name = inputs.name.value;
        if (name === "") {
            displayResult(results.name, "필수 정보입니다.", errorColor);
            validation.name = false;
        } else if (!regex.name.test(name)) {
            displayResult(results.name, "이름 형식에 맞지 않습니다.", errorColor);
            validation.name = false;
        } else {
            results.name.innerText = "";
            validation.name = true;
        }
    }

    function validateCompany() {
        let company = inputs.company.value;
        if (company === "") {
            displayResult(results.company, "필수 정보입니다.", errorColor);
            validation.company = false;
        } else {
            results.company.innerText = "";
            validation.company = true;
        }
    }

    function validateCeo() {
        let ceo = inputs.ceo.value;
        if (ceo === "") {
            displayResult(results.ceo, "필수 정보입니다.", errorColor);
            validation.ceo = false;
        } else if (!regex.ceo.test(ceo)) {
            displayResult(results.ceo, "이름 형식에 맞지 않습니다.", errorColor);
            validation = false;
        } else {
            results.ceo.innerText = "";
            validation.ceo = true;
        }
    }

    function validateBizNum1() {
        let bizNum1 = inputs.bizNum1.value;
        if (bizNum1 === "" || bizNum1.length !== 3) {
            displayResult(results.bizNum, "사업자 등록번호 앞자리는 3자입니다. 다시 입력해주세요.", errorColor);
            inputs.bizNum2.disabled = true;
            inputs.bizNum3.disabled = true;
            validation.bizNum1 = false;
            validation.bizNum = false;
        }
    }

    function validateBizNum2() {
        let bizNum2 = inputs.bizNum2.value;
        if (bizNum2 === "" || bizNum2.length !== 2) {
            displayResult(results.bizNum, "사업자 등록번호 뒷자리는 2자입니다. 다시 입력해주세요.", errorColor);
            inputs.bizNum1.disabled = true;
            inputs.bizNum3.disabled = true;
            validation.bizNum2 = false;
            validation.bizNum = false;
        }
    }

    function validateBizNum3() {
        let bizNum3 = this.value;
        if (bizNum3 === "" || bizNum3.length !== 5) {
            displayResult(results.bizNum, "사업자 등록번호 뒷자리는 5자입니다. 다시 입력해주세요.", errorColor);
            inputs.bizNum1.disabled = true;
            inputs.bizNum2.disabled = true;
            validation.bizNum3 = false;
            validation.bizNum = false;
        }
    }

    function focusBizNum1() {
        let bizNum1 = this.value;
        if (bizNum1.length === 3) {
            inputs.bizNum2.disabled = false;
            inputs.bizNum3.disabled = false;
            results.bizNum.innerText = "";
            validation.bizNum1 = true;
            inputs.bizNum2.focus();
            bizNumOkValidation();
        }
    }
    function focusBizNum2() {
        let bizNum2 = this.value;
        if (bizNum2.length === 2) {
            inputs.bizNum1.disabled = false;
            inputs.bizNum3.disabled = false;
            results.bizNum.innerText = "";
            validation.bizNum2 = true;
            inputs.bizNum3.focus();
            bizNumOkValidation();
        }
    }
    function focusBizNum3() {
        let bizNum3 = this.value;
        if (bizNum3.length === 5) {
            inputs.bizNum1.disabled = false;
            inputs.bizNum2.disabled = false;
            results.bizNum.innerText = "";
            validation.bizNum3 = true;
            bizNumOkValidation()
        }
    }

    function validateReportNumber() {
        let reportNumber = inputs.reportNumber.value;
        if (reportNumber === "" || !regex.reportNumber.test(reportNumber)) {
            displayResult(results.reportNumber, "번호를 정확하게 입력해주세요. \"년도-지역-일련번호\" 형식", errorColor);
            validation.reportNumber = false;
        } else {
            displayResult(results.reportNumber, "\"년도-지역-4자리번호\" 형식, 예) 2017-경기성남-0011", basicColor);
            validation.reportNumber = true;
        }
    }

    function validatePhoneNumber() {
        let middleNumber = inputs.middleNumber.value;
        let lastNumber = inputs.lastNumber.value;
        if (middleNumber === "" || middleNumber.length !== 4 || lastNumber === "" || lastNumber.length !== 4) {
            displayResult(results.phoneNumber, "전화번호를 정확히 입력해 주세요.", errorColor);
            validation.phoneNumber = false;
        } else {
            displayResult(results.phoneNumber, "", "");
            validation.phoneNumber = true;
        }
    }

    function focusOnLastNumber() {
        let middleNumber = inputs.middleNumber.value;
        if (middleNumber.length === 4) {
            inputs.lastNumber.focus();
        }
    }

    function validationAddress() {
        let zipCode = inputs.zipCode.value;
        let address = inputs.address.value;
        let detailAddress = inputs.detailAddress.value;
        if (zipCode === "" || address === "" || detailAddress === "") {
            displayResult(results.address, "주소를 입력해 주세요.", errorColor);
        } else {
            results.address.innerText = "";
            validation.address = true;
        }
    }

    function validateForm() {
        const validations = [
            { isValid: validation.loginId, message: '아이디를 확인해주세요.', input: inputs.loginId },
            { isValid: validation.password, message: '비밀번호를 확인해주세요.', input: inputs.password },
            { isValid: validation.confirmPassword, message: '비밀번호가 틀립니다.', input: inputs.confirmPass },
            { isValid: validation.name, message: '이름을 확인해주세요.', input: inputs.name },
            { isValid: validation.company, message: '회사명을 확인해주세요.', input: inputs.company },
            { isValid: validation.ceo, message: '대표자 이름을 확인해주세요.', input: inputs.ceo },
            { isValid: validation.bizNum, message: '사업자등록번호를 확인해주세요.', input: inputs.bizNum1 },
            { isValid: validation.reportNumber, message: '통신판매업신고 번호를 확인해주세요.', input: inputs.reportNumber },
            { isValid: validation.phoneNumber, message: '휴대폰 번호를 확인해주세요.', input: inputs.middleNumber },
            { isValid: validation.address, message: '주소를 확인해주세요.' }
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

    function gatherMemberData() {
        const loginId = inputs.loginId.value;
        const password = inputs.password.value;
        const name = inputs.name.value;
        const company = inputs.company.value;
        const ceo = inputs.ceo.value;
        const businessRegistrationNumber = inputs.bizNum1.value + "-" + inputs.bizNum2.value + "-" + inputs.bizNum3.value;
        const reportNumber = inputs.reportNumber.value;
        const phoneNumber = inputs.areaNumber.value + "-" + inputs.middleNumber.value + "-" + inputs.lastNumber.value;
        const zipCode = inputs.zipCode.value;
        const address = inputs.address.value;
        const detailAddress = inputs.detailAddress.value;

        return {
            loginId,
            password,
            name,
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

    function bizNumOkValidation() {
        if (validation.bizNum1 && validation.bizNum2 && validation.bizNum3) {
            validation.bizNum = true;
        } else {
            validation.bizNum = false;
        }
    }

    ///////////// ajax ///////////////

    // 아이디 중복 ajax
    function ajaxCheckSellerId(loginId) {
        $.ajax({
            url: "/kmarket/member/register/checkLoginId",
            method: "POST",
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({"loginId": loginId}),
            success: function (data) {
                if (data.status === 1) {
                    displayResult(results.loginId, data.message, errorColor);
                    validation.loginId = false;
                } else if (data.status === 0) {
                    displayResult(results.loginId, data.message, successColor);
                    validation.loginId = true;
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error(xhr.responseText);
                alert("잘못된 요청입니다. 다시 실행해주세요.");
                validation.loginId = false;
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