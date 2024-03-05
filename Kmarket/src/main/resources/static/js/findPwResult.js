window.onload = function() {

    // 폰트 색 설정
    const errorColor = "rgb(240,13,56)";
    const successColor = "rgb(13,136,241)";

    let passOk = false;
    let passConfirmOk = false;

    const loginIdInput = document.getElementById('loginId');
    const passInput = document.getElementById('password');
    const passConfirmInput = document.getElementById('confirmPass');
    const submit = document.getElementById('submit');

    const passRegex = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\W)(?=\S+$).{7,15}$/;

    const passResult = document.querySelector('.passwordResult');
    const passConfirmResult = document.querySelector('.confirmPassResult');

    passInput.addEventListener('focusout', function () {
        let password = passInput.value;
        let confirmPass = passConfirmInput.value;

        if (password === "") {
            displayResult(passResult, "8~16자의 영문 대/소문자, 숫자, 특수문자 조합이어야 합니다.", errorColor);
            passOk = false;
        } else if (!passRegex.test(password)) {
            displayResult(passResult, "비밀번호 조합 기준에 적합하지 않습니다.", errorColor);
            passOk = false;
        } else {
            displayResult(passResult, "적절한 비밀번호 입니다.", successColor);
            passOk = true;
        }

        if (confirmPass === "") {
            return;
        }
        if (password !== confirmPass) {
            displayResult(passConfirmResult, "비밀번호가 일치하지 않습니다.", errorColor);
            passConfirmOk = false;
        } else {
            displayResult(passConfirmResult, "비밀번호가 일치합니다.", successColor);
            passConfirmOk = true;
        }
    });

    passConfirmInput.addEventListener('focusout', function () {
        let confirmPass = passConfirmInput.value;
        let password = passInput.value;
        if (confirmPass === "") {
            displayResult(passConfirmResult, "비밀번호 확인이 필요합니다.", errorColor);
            passConfirmOk = false;
        } else if (confirmPass !== password) {
            displayResult(passConfirmResult, "비밀번호가 일치하지 않습니다.", errorColor);
            passConfirmOk = false;
        } else {
            displayResult(passConfirmResult, "비밀번호가 일치합니다.", successColor);
            passConfirmOk  = true;
        }
    });

    submit.addEventListener('click', function (event) {
        event.preventDefault();

        let loginId = loginIdInput.value;
        let password = passInput.value;

        console.log(loginId + ", " + password);
        console.log(passOk + ", " + passConfirmOk);

        if (!passOk || !passConfirmOk) {
            alert('비밀번호를 정확히 입력해주세요.');
        } else {

            /**
             * 회원 비밀번호 변경
             */
            $.ajax({
                url: "/kmarket/members/resetPw",
                method: "PUT",
                contentType: "application/json",
                dataType: 'json',
                data: JSON.stringify({"loginId" : loginId, "password": password}), // 객체 형태로 전송
                success: function (data) {
                    if (data.status === 1) {
                        alert(data.message);
                        window.location.href = "/kmarket/members/login";
                    } else {
                        alert(data.message);
                        window.location.href = "/kmarket/members/findPw";
                    }
                },
                error: function (xhr, textStatus, errorThrown) {
                    alert("잘못된 요청입니다. 다시 실행해주세요.");
                }
            });
        }
    });

    function displayResult(element, message, color) {
        element.innerText = "\u00a0" + message;
        element.style.color = color;
    }
}