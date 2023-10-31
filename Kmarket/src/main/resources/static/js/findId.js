window.onload = function () {
    const errorColor = "rgb(240,13,56)";
    const successColor = "rgb(13,136,241)";

    let authCode = null; // 인증 번호
    let current_time = 0; // 발송 후 지난 초
    const fiveMinutes = 5 * 60; // 초 단위로 변환
    let minutes,seconds; // 유효시간
    let timer_thread;

    let validation = {
        name: false,
        email: false,
        code: false // 코드 유효성 검증
    };

    const form = document.getElementById('form');
    const displayTimer = document.getElementById('timer');
    const input = {
        name: document.getElementById('name'),
        emailPrefix: document.getElementById('emailPrefix'),
        emailDomain: document.getElementById('emailDomain'),
        emailDomainSelect: document.getElementById('domainSelect'),
        emailAuthDiv: document.getElementById('emailAuthDiv'),
        authCode: document.getElementById('authCode')
    };

    const btn = {
        emailAuth: document.getElementById('btnEmailAuth'),
        emailConfirm: document.getElementById('btnEmailConfirm'),
        resendEmail: document.getElementById('btnReSendEmail'),
        searchId: document.getElementById('btnSearchId')
    };

    const regex = {
        name: /^(?:[가-힣\s]{2,20}|[a-zA-Z\s]{2,20})$/,
        email: /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i
    };

    const result = {
        name: document.querySelector('.nameResult'),
        email: document.querySelector('.emailResult'),
        emailAuth: document.querySelector('.emailAuthResult')
    };

    input.name.addEventListener('focusout', function () {
        const name = this.value;
        if (name === "") {
            displayResult(result.name, "이름을 입력해주세요.", errorColor);
            validation.name = false;
        } else if (!regex.name.test(name)) {
            displayResult(result.name, "형식에 맞게 입력해주세요.", errorColor);
            validation.name = false;
        } else {
            result.name.innerText = "";
            validation.name = true;
        }
    });

    input.emailPrefix.addEventListener('focusout', validateEmail);
    input.emailDomain.addEventListener('focusout', validateEmail);
    input.emailDomainSelect.addEventListener('change', validateEmail);

    // 인증코드 보내기 버튼
    btn.emailAuth.addEventListener('click', function () {
        const name = input.name.value;
        const emailPrefix = input.emailPrefix.value;
        const emailDomain = input.emailDomain.value;
        const email = emailPrefix + "@" + emailDomain;

        // 유효성 검사
        if (name === "" || emailPrefix === "" || emailDomain === "" || !validation.email) {
            alert('이름 또는 이메일을 다시 확인해주세요.');
        } else {
            // 해당 회원이 있는지 찾기
            $.ajax({
                url: "/kmarket/member/checkMemberNameAndEmail",
                method: 'POST',
                contentType: "application/json",
                dataType: 'json',
                data: JSON.stringify({"name" : name , "email" : email}),
                success: function (data) {
                    console.log(data.status);
                    if (data.status === 1) {
                        // 인증코드 발송 O
                        emailCodeSend(email);
                    } else {
                        // 인증코드 발송 X
                        alert(data.message);
                    }
                },
                error: function (xhr, textStatus, errorThrown) {
                    console.error(xhr.responseText);
                    alert("잘못된 요청입니다. 다시 실행해주세요.");
                }
            });
        }
    });

    // 이메일 코드 보내기
    function emailCodeSend(email) {
        // 인증번호 전송
        alert('인증번호 발송 요청이 완료되었습니다.\n인증번호가 오지 않는 경우, 입력한 이름/이메일 주소를 확인 후 다시 요청해주세요.');

        input.emailAuthDiv.style.display = "block"; // 인증 번호 기입란 보이기
        btn.emailAuth.style.display = "none"; // 인증번호 보내기 버튼 없애기

        startTimer(fiveMinutes, displayTimer); // 타이머 5분 설정
        ajaxEmailCodeSend(email); // ajax 보내기
    }

    // 인증코드 재전송 버튼
    btn.resendEmail.addEventListener('click', function () {
        const emailPrefix = input.emailPrefix.value;
        const emailDomain = input.emailDomain.value;
        const email = emailPrefix + "@" + emailDomain;

        alert('인증번호 발송 요청이 완료되었습니다.\n인증번호가 오지 않는 경우, 입력한 이름/이메일 주소를 확인 후 다시 요청해주세요.');
        btn.emailConfirm.disabled = false;

        // 재전송 버튼 누를 시 ajax 로 다시 이메일 코드 전송
        ajaxEmailCodeSend(email);

        // 만료시간 다시 업데이트
        startTimer(fiveMinutes, displayTimer);
    });

    // 이메일 인증코드 확인
    btn.emailConfirm.addEventListener('click', function () {
        let authCode = input.authCode.value;

        if (authCode === "") {
            alert('인증코드를 입력해주세요.');
            return;
        }

        $.ajax({
            url: "/kmarket/member/codeConfirm",
            method: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({"authCode" : authCode}),
            success: function (data) {
                if (data.status === 200) {
                    alert('인증되었습니다. 아이디 찾기를 눌려주세요.');
                    timer_stop();
                    displayResult(displayTimer, "인증되었습니다.", successColor);
                    input.authCode.disabled = true;
                    btn.emailConfirm.disabled = true;
                    btn.resendEmail.disabled = true;
                    btn.searchId.disabled = false;
                    validation.code = true; // 코드 유효성 만족
                } else {
                    alert('인증번호가 만료되었거나 올바르지 않습니다. 다시 시도하여주세요.');
                    validation.code = false;
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                alert("잘못된 요청입니다. 다시 실행해주세요.");
            }
        });
    });

    // // 아이디 찾기 버튼 클릭
    // btn.searchId.addEventListener('click', function () {
    //     let name = input.name.value;
    //     let email = input.emailPrefix.value + "@" + input.emailDomain.value;
    //
    //     if (validation.name && validation.email && validation.code) {
    //         $.ajax({
    //             url: "/kmarket/member/searchId",
    //             method: 'POST',
    //             contentType: "application/json",
    //             dataType: 'json',
    //             data: JSON.stringify({"name": name, "email": email}),
    //             // success: function (data) {
    //             //
    //             // },
    //             // error: function (xhr, textStatus, errorThrown) {
    //             //     alert("잘못된 요청입니다. 다시 실행해주세요.");
    //             // }
    //         });
    //     } else {
    //         alert('회원 정보를 확인해주세요.');
    //     }
    // });


    // ajax 이메일 코드 전송
    function ajaxEmailCodeSend(email) {
        // 인증 코드 보내기
        $.ajax({
            url: "/kmarket/member/mailConfirm",
            method: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({"email" : email}),
            success: function (data) {
                console.log(data.message);
                validation.email = true;
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error(xhr.responseText);
                alert("잘못된 요청입니다. 다시 실행해주세요.");
                validation.email = false;
            }
        });
    }


    // timer display
    function startTimer(fiveMinutes, displayTimer) {
        let timer = fiveMinutes;
        
        // 1초마다 실행
        timer_thread = setInterval(function () {
                    minutes = parseInt(timer / 60, 10);
                    seconds = parseInt(timer % 60, 10);

                    minutes = minutes < 10 ? "0" + minutes : minutes;
                    seconds = seconds < 10 ? "0" + seconds : seconds;

                    displayResult(displayTimer, minutes + "분 " + seconds + "초 남았습니다.", errorColor);

                    // 타이머 끝 나면
                    if (--timer < 0) {
                        timer_stop();
                        displayResult(displayTimer, "인증코드가 만료되었습니다.", errorColor);
                        btn.emailConfirm.disabled = true;
                    }
        }, 1000);
    }

    // 타이머 종료
    function timer_stop(){
        // 타이머 종료
        clearInterval(timer_thread)
        // 유효시간 만료
        validation.code = false
    }

    function validateEmail() {
        const emailPrefix = input.emailPrefix.value;
        const emailDomain = input.emailDomain.value;
        const email = emailPrefix + "@" + emailDomain;

        if (emailPrefix === "" || emailDomain === "" || !regex.email.test(email)) {
            displayResult(result.email, "이메일 주소를 다시 확인해주세요.", errorColor);
            validation.email = false;
        } else {
            result.email.innerText = "";
            validation.email = true;
        }
    }

    function displayResult(element, message, color) {
        element.innerText = "\u00a0" + message;
        element.style.color = color;
    }
};