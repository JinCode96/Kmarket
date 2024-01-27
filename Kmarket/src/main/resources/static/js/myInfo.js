window.onload = function () {

    const emailRegex = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;


    const btnChangeEmail = document.getElementById('btnChangeEmail');

    /**
     * 이메일 수정
     */
    btnChangeEmail.addEventListener('click', function () {
        let emailPrefix = document.getElementById('emailPrefix').value;
        let emailDomain = document.getElementById('emailDomain').value;

        let email = emailPrefix + "@" + emailDomain;

        if (emailPrefix === "" || emailDomain === "") {
            alert('수정할 이메일을 입력해주세요.');
            return;
        }
        if (!emailRegex.test(email)) {
            alert('이메일을 올바른 형태로 입력해주세요.');
            return;
        }
        // 이메일 중복검사
        $.ajax({
            url: "/kmarket/member/register/checkEmail",
            method: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({"email" : email}),
            success: function (data) {
                if (data.status === 1) {
                    alert('이미 사용중인 이메일입니다.');
                } else if (data.status === 0) {
                    // alert('사용가능한 이메일입니다.');
                    // 이메일 수정 ajax
                    ajaxUpdateEmail(email);
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                alert("이메일 수정중 문제가 발생했습니다. 다시 시도해주세요.");
            }
        });
    });
};

const btnChangeHp = document.getElementById('btnChangeHp');
const btnChangeAddress = document.getElementById('btnChangeAddress');
const btnWithdraw = document.getElementById('btnWithdraw');

const phoneNumberRegex = /^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$/i;

/**
 * 휴대폰 번호 수정
 */
btnChangeHp.addEventListener('click', function () {

    const phoneNumber = document.getElementById('phoneNumber').value.replace(/^(\d{2,3})(\d{3,4})(\d{4})$/, `$1-$2-$3`);
    console.log("phoneNumber = " + phoneNumber);

    if (phoneNumber === "") {
        alert('휴대폰 번호를 입력해주세요.');
        return;
    }
    if (!phoneNumberRegex.test(phoneNumber)) {
        alert('휴대폰 번호를 정확히 입력해주세요.');
        return;
    }
    $.ajax({
        url: "/kmarket/my/updatePhoneNumber",
        method: 'PUT',
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify({"phoneNumber" : phoneNumber}),
        success: function (data) {
            if (data.status === 200) {
                alert('휴대폰 번호를 수정하였습니다.');
            }else if (data.status === 300) {
                alert('이미 등록되어있는 휴대폰 번호입니다.');
            } else if (data.status === 400) {
                alert('휴대폰 번호 수정에 실패하였습니다. 다시 시도해주세요.');
            }
        },
        error: function (data) {
            alert("휴대폰번호 수정 중 문제가 발생했습니다. 다시 시도해주세요.");
        }
    });
});

/**
 * 주소 수정
 */
btnChangeAddress.addEventListener('click', function () {
    const zipCode = document.getElementById('zipCode').value;
    const address = document.getElementById('address').value;
    const detailAddress = document.getElementById('detailAddress').value;

    if (zipCode === "" || address === ""){
        alert('주소를 입력하세요');
        return;
    }
    if (detailAddress === "") {
        alert('상세 주소를 입력하세요');
        return;
    }
    $.ajax({
        url: "/kmarket/my/updateAddress",
        method: 'PUT',
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify({"zipCode" : zipCode, "address" : address, "detailAddress" : detailAddress}),
        success: function (data) {
            if (data.status === 200) {
                alert('주소를 수정하였습니다');
            } else if (data.status === 400) {
                alert('주소 수정에 실패하였습니다. 다시 시도해주세요.');
            }
        },
        error: function (data) {
            alert("주소 수정 중 문제가 발생했습니다. 다시 시도해주세요.");
        }
    });
});

function ajaxUpdateEmail(email) {
    console.log("email = " + email);
    $.ajax({
        url: "/kmarket/my/updateEmail",
        method: 'PUT',
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify({"email" : email}),
        success: function (data) {
            if (data.status === 200) {
                alert('이메일을 수정하였습니다.');
            } else if (data.status === 400) {
                alert('이메일 수정에 실패하였습니다. 다시 시도해주세요.');
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            alert("이메일 수정중 문제가 발생했습니다. 다시 시도해주세요.");
        }
    });
}