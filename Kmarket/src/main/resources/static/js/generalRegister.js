window.onload = function() {
    // 폰트 색 설정
    const errorColor = "rgb(240,13,56)";
    const successColor = "rgb(13,136,241)";

    // Define validation flags
    let formValid = false;
    let validation = {
        loginId: false,
        password: false,
        confirmPassword: false,
        name: false,
        email: false,
        phoneNumber: false,
        address: false
    };

    // Get input elements
    const form = document.getElementById('generalForm');
    const inputs = {
        loginId: document.getElementById('loginId'),
        password: document.getElementById('password'),
        confirmPassword: document.getElementById('confirmPass'),
        name: document.getElementById('name'),
        emailPrefix: document.getElementById('emailPrefix'),
        emailDomain: document.getElementById('emailDomain'),
        emailDomainSelect: document.getElementById('domainSelect'),
        areaNumberSelect: document.getElementById('areaNumber'),
        middleNumber: document.getElementById('middleNumber'),
        lastNumber: document.getElementById('lastNumber'),
        zipCode: document.getElementById('zipCode'),
        address: document.getElementById('address'),
        detailAddress: document.getElementById('detailAddress')
    };

    // Define regular expressions
    const regex = {
        loginId: /^[a-z][a-z0-9]{5,12}$/,
        password: /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\W)(?=\S+$).{7,15}$/,
        name: /^(?:[가-힣\s]{2,20}|[a-zA-Z\s]{2,20})$/,
        email: /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i
    };

    // Result elements
    const results = {
        loginId: document.querySelector('.loginIdResult'),
        password: document.querySelector('.passwordResult'),
        confirmPassword: document.querySelector('.confirmPassResult'),
        name: document.querySelector('.nameResult'),
        email: document.querySelector('.emailResult'),
        phoneNumber: document.querySelector('.numberResult'),
        address: document.querySelector('.addressResult')
    };

    // Add event listeners
    inputs.loginId.addEventListener('focusout', validateLoginId);
    inputs.password.addEventListener('focusout', validatePassword);
    inputs.confirmPassword.addEventListener('focusout', validateConfirmPassword);
    inputs.name.addEventListener('focusout', validateName);
    inputs.emailPrefix.addEventListener('focusout', handleEmailValidationAndCheck);
    inputs.emailDomain.addEventListener('focusout', handleEmailValidationAndCheck);
    inputs.emailDomainSelect.addEventListener('change', handleEmailValidationAndCheck);
    inputs.middleNumber.addEventListener('focusout', validatePhoneNumber);
    inputs.middleNumber.addEventListener('input', focusOnLastNumber);
    inputs.lastNumber.addEventListener('focusout', validatePhoneNumber);
    inputs.detailAddress.addEventListener('focusout', validateAddress);

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        formValid = validateForm();
        if (formValid) {
            const memberData = gatherMemberData();
            ajaxSendMemberToServer(memberData);
        }
    });

    ////////////// function /////////////

    function validateLoginId() {
        let loginId = inputs.loginId.value;
        if (loginId === "") { // 공백
            displayResult(results.loginId,"필수 정보입니다.", errorColor);
            validation.loginId = false;
        }else if (!regex.loginId.test(loginId)) { // 정규식 만족
            displayResult(results.loginId, "회원 아이디(ID)는 띄어쓰기 없이 6~13자리의 영문자와 숫자 조합만 가능합니다", errorColor);
            validation.loginId = false;
        } else {
            ajaxCheckId(loginId);
        }
    }

    function validatePassword() {
        let password = inputs.password.value;
        let confirmPass = inputs.confirmPassword.value;
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
            displayResult(results.confirmPassword, "비밀번호가 일치하지 않습니다.", errorColor);
            validation.confirmPassword = false;
        } else {
            displayResult(results.confirmPassword, "비밀번호가 일치합니다.", successColor);
            validation.confirmPassword = true;
        }
    }

    function validateConfirmPassword() {
        let confirmPass = inputs.confirmPassword.value;
        let password = inputs.password.value;
        if (confirmPass === "") {
            displayResult(results.confirmPassword, "비밀번호 확인이 필요합니다.", errorColor);
            validation.confirmPassword = false;
        } else if (confirmPass !== password) {
            displayResult(results.confirmPassword, "비밀번호가 일치하지 않습니다.", errorColor);
            validation.confirmPassword = false;
        } else {
            displayResult(results.confirmPassword, "비밀번호가 일치합니다.", successColor);
            validation.confirmPassword  = true;
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

    function handleEmailValidationAndCheck() {
        if (validateEmail()) {
            ajaxCheckEmail(inputs.emailPrefix.value + "@" + inputs.emailDomain.value);
        } else {
            validation.email = false;
        }
    }

    function validateEmail() {
        const emailPrefix = inputs.emailPrefix.value;
        const emailDomain = inputs.emailDomain.value;
        const email = emailPrefix + "@" + emailDomain;

        if (emailPrefix === "" || emailDomain === "" || !regex.email.test(email)) {
            displayResult(results.email, "이메일 주소를 다시 확인해주세요.", errorColor);
            return false;
        }
        return true;
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

    function validateAddress() {
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

    function gatherMemberData() {
        const loginId = inputs.loginId.value;
        const password = inputs.password.value;
        const name = inputs.name.value;
        const email = inputs.emailPrefix.value + "@" + inputs.emailDomain.value;
        const phoneNumber = inputs.areaNumberSelect.value + "-" + inputs.middleNumber.value + "-" + inputs.lastNumber.value;
        const zipCode = inputs.zipCode.value;
        const address = inputs.address.value;
        const detailAddress = inputs.detailAddress.value;

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

    function validateForm() {
        const validations = [
            { isValid: validation.loginId, message: '아이디를 확인해주세요.', input: inputs.loginId },
            { isValid: validation.password, message: '비밀번호를 확인해주세요.', input: inputs.password },
            { isValid: validation.confirmPassword, message: '비밀번호가 틀립니다.', input: inputs.confirmPassword },
            { isValid: validation.name, message: '이름을 확인해주세요.', input: inputs.name },
            { isValid: validation.email, message: '이메일을 확인해주세요.', input: inputs.emailPrefix },
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

    /////////////// ajax //////////////

    function ajaxCheckId(loginId) {
        $.ajax({
            url: "/kmarket/member/register/checkLoginId",
            method: "POST",
            contentType: "application/json", // 안 붙이면 "="이 자꾸 붙음..
            dataType: 'json', // 서버에서 json 응답 기대
            data: JSON.stringify({"loginId": loginId}), // 객체 형태로 전송
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
                alert("잘못된 요청입니다. 다시 실행해주세요.");
                validation.loginId = false;
            }
        });
    }

    function ajaxCheckEmail(email) {
        $.ajax({
            url: "/kmarket/member/register/checkEmail",
            method: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({"email" : email}),
            success: function (data) {
                if (data.status === 1) {
                    displayResult(results.email, data.message, errorColor);
                    validation.email = false;
                } else if (data.status === 0) {
                    displayResult(results.email, data.message, successColor);
                    validation.email = true;
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error(xhr.responseText);
                alert("잘못된 요청입니다. 다시 실행해주세요.");
                validation.email = false;
            }
        });
    }

    function ajaxSendMemberToServer(member) {
        $.ajax({
            url: "/kmarket/member/register/general",
            method: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(member),
            success: function (data) {
                if (data.status === 1) {
                    alert(data.message);
                    window.location.href = "/kmarket/member/loginForm";
                } else {
                    alert(data.message);
                    window.location.href = "/kmarket/member/join";
                }
            },
            error: function (data) {
                alert(data.message);
            }
        });
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