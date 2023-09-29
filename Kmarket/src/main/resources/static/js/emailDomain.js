
/* 이메일 도메인 select 자동 입력 */
document.addEventListener('DOMContentLoaded', function () {
    const emailDomainInput = document.getElementById('emailDomain');
    const domainSelect = document.getElementById('domainSelect');

    // select 요소에서 도메인을 선택할 때 이벤트 처리
    domainSelect.addEventListener('change', function () {
        const selectedDomain = domainSelect.value;
        emailDomainInput.value = selectedDomain; // 선택한 도메인을 입력 필드에 설정
    });
});