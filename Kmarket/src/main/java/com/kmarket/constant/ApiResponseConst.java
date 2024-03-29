package com.kmarket.constant;

public final class ApiResponseConst {
    public static final String TERMS_OK = "이용약관 동의 완료.";
    public static final String BAD_REQUEST = "잘못된 요청입니다.";
    public static final String NOT_FOUND = "해당 주소를 찾을 수 없습니다.";
    public static final String REGISTER_OK = "회원가입이 완료되었습니다. 로그인 해주세요.";
    public static final String REGISTER_NOT_OK = "회원가입에 실패하였습니다. 다시 회원가입을 진행해주세요.";
    public static final String ID_OK = "사용 가능한 아이디 입니다.";
    public static final String ID_NOT_OK = "이미 사용중인 아이디 입니다.";
    public static final String EMAIL_OK = "사용 가능한 이메일 입니다.";
    public static final String EMAIL_NOT_OK = "이미 사용중인 이메일입니다.";
    public static final String EMAIL_CODE_OK = "이메일 코드를 전송하였습니다.";
    public static final String MEMBER_FOUND = "입력한 정보와 일치하는 회원이 있습니다.";
    public static final String MEMBER_NOT_FOUND = "입력한 정보와 일치하는 회원정보가 없습니다. 이름/이메일 주소를 확인해주세요.";
    public static final String CODE_OK = "코드 확인 완료되었습니다.";
    public static final String CODE_NOT_OK = "코드 맞지 않거나 만료된 코드입니다.";
    public static final String PASSWORD_UPDATE_OK = "비밀번호 변경이 완료되었습니다. 새로 로그인해주세요.";
    public static final String PASSWORD_UPDATE_NOT_OK = "비밀번호 변경에 실패하였습니다. 다시 시도해주세요.";
    public static final String PRODUCT_DELETE_OK = "상품이 삭제되었습니다.";
    public static final String PRODUCT_DELETE_NOT_OK = "상품 삭제 시 문제가 발생하였습니다. 다시 시도해주세요.";
    public static final String PRODUCT_NOT_FOUND = "상품이 존재하지 않습니다.";
    public static final String PRODUCT_CHANGE_OK = "상품 수량 변경 완료";
    public static final String MAX_PURCHASE_PER_PERSON = "상품의 1인당 최대 구매 가능 개수는 1일 동안 100개 입니다.";
    public static final String ADD_TO_CART_SUCCESS = "장바구니에 상품이 추가되었습니다.\n장바구니로 이동하시겠습니까?";
    public static final String ADD_TO_CART_FAILED = "상품을 장바구니에 추가하는데 실패하였습니다. 다시 시도해 주세요.";


    public static final int SUCCESS = 200;
    public static final int FAIL = 400;

}
