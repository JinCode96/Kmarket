package com.kmarket.kakaopay;

import com.kmarket.kakaopay.KakaoApproveResponse;
import com.kmarket.kakaopay.KakaoReadyResponse;
import com.kmarket.kakaopay.ProductOrderCartDTO;
import com.kmarket.kakaopay.ProductOrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 카카오 페이 api
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KakaoPayService {
    static final String cid = "TC0ONETIME"; // 가맹점 테스트 코드
    static final String admin_Key = System.getenv("KAKAO_ADMIN_KEY"); // 환경 변수에서 가져오기
    private static final String KAKAO_PAY_READY_URL = "https://kapi.kakao.com/v1/payment/ready"; // 1차 요청 주소
    private static final String KAKAO_PAY_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve"; // 승인 요청 주소
    private KakaoReadyResponse kakaoReady;

    /**
     * 결제 준비
     */
    public KakaoReadyResponse kakaoPayReady(ProductOrderDTO productOrderDTO) {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("cid", cid); // 가맹점 코드
        parameters.add("partner_order_id", "partner_order_id"); // 가맹점 주문 번호
        parameters.add("partner_user_id", "partner_user_id"); // 가맹점 회원 아이디
        parameters.add("item_name", productOrderDTO.getProductName()); // 상품명
        parameters.add("item_code", String.valueOf(productOrderDTO.getProductId())); // 상품 코드
        parameters.add("quantity", String.valueOf(productOrderDTO.getQuantity())); // 상품 수량
        parameters.add("total_amount", String.valueOf(productOrderDTO.getTotalAmount())); // 총 금액
        parameters.add("tax_free_amount", "0"); // 상품 비과세 금액
        parameters.add("approval_url", "http://localhost:8080/kmarket/product/orderApproval"); // 성공 시 redirect url
        parameters.add("cancel_url", "http://localhost:8080/kmarket/product/directOrder?productId=" + productOrderDTO.getProductId() + "&quantity=" + productOrderDTO.getQuantity()); // 취소 시 redirect url
        parameters.add("fail_url", "http://localhost:8080/kmarket"); // 실패 시 redirect url

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();
        kakaoReady = restTemplate.postForObject(KAKAO_PAY_READY_URL, entity, KakaoReadyResponse.class); // 외부에 보낼 url

        return kakaoReady;
    }

    /**
     * 결제 준비 (장바구니)
     */
    public KakaoReadyResponse kakaoPayReady(ProductOrderCartDTO productOrderCartDTO) {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("cid", cid); // 가맹점 코드
        parameters.add("partner_order_id", "partner_order_id"); // 가맹점 주문 번호
        parameters.add("partner_user_id", "partner_user_id"); // 가맹점 회원 아이디
        parameters.add("item_name", productOrderCartDTO.getProductName()); // 상품명
        parameters.add("item_code", String.valueOf(productOrderCartDTO.getIdAndQuantities().get(0).getProductId())); // 상품 코드
        parameters.add("quantity", String.valueOf(productOrderCartDTO.getIdAndQuantities().size())); // 상품 수량
        parameters.add("total_amount", String.valueOf(productOrderCartDTO.getTotalAmount())); // 총 금액
        parameters.add("tax_free_amount", "0"); // 상품 비과세 금액
        parameters.add("approval_url", "http://localhost:8080/kmarket/product/orderApprovalCart"); // 성공 시 redirect url
        parameters.add("cancel_url", "http://localhost:8080/kmarket/"); // 취소 시 redirect url
        parameters.add("fail_url", "http://localhost:8080/kmarket"); // 실패 시 redirect url todo 오류페이지 하나 만들기

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();
        kakaoReady = restTemplate.postForObject(KAKAO_PAY_READY_URL, entity, KakaoReadyResponse.class); // 외부에 보낼 url

        return kakaoReady;
    }

    /**
     * 결제 완료 승인
     */
    public KakaoApproveResponse approveResponse(String pgToken) {

        // 카카오 요청
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", kakaoReady.getTid());
        parameters.add("partner_order_id", "partner_order_id");
        parameters.add("partner_user_id", "partner_user_id");
        parameters.add("pg_token", pgToken);

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForObject(KAKAO_PAY_APPROVE_URL, entity, KakaoApproveResponse.class); // 외부에 보낼 url
    }

    /**
     * 카카오 요구 헤더값
     */
    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "KakaoAK " + admin_Key;
        httpHeaders.add("Authorization", auth);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        return httpHeaders;
    }
}
