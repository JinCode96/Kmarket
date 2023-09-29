package com.kmarket.regex;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

public class RegexTest {

    @Test
    void idTest() {
        String input = "asdfasdf1234";
        String regexPattern = "^[a-z]+[a-z0-9]{1,10}$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        assertThat(matcher.matches()).isTrue();
    }

    @Test
    void passTest() {
        String input = "mylove9435!!";
        String regexPattern = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        assertThat(matcher.matches()).isTrue();
    }

    @Test
    void nameTest() {
        String input = "안녕하세요";
        String regexPattern = "^[가-힣]{1,5}$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        assertThat(matcher.matches()).isTrue();
    }

    @Test
    void phoneTest() {
        String input = "1223";
        String regexPattern = "^[0-9]{3,4}$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        assertThat(matcher.matches()).isTrue();
    }

    @Test
    void emailTest() {
        String input = "asdf.awerf";
        String regexPattern = "^[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        assertThat(matcher.matches()).isTrue();
    }

    @Test
    void name2Test() {
        // 정규식
        String regex = "^(?:[\\u0000-\\u007F]|[\\u0080-\\u07FF]|[\\u0800-\\uFFFF]){1,50}$";
        Pattern pattern = Pattern.compile(regex);

        // 테스트할 문자열
        String testString1 = "Hello, World123141!"; // 영문 13자 (13 바이트)
        String testString2 = "ㅁㅎㄷㅁㄴㅇㅀ"; // 한글 5자 (15 바이트)
        String testString3 = "1234567890".repeat(5); // 숫자 50자 (50 바이트)
        String testString4 = "𠜎𠜱𠝹𠱓𠱸𠲖𠴱".repeat(4); // 4개의 4바이트 유니코드 문자 (16 바이트)
        String testString5 = "!@#!$%!%!@#!#";

        assertThat(pattern.matcher(testString1).matches()).isTrue();
        assertThat(pattern.matcher(testString2).matches()).isTrue();
        assertThat(pattern.matcher(testString3).matches()).isTrue();
        assertThat(pattern.matcher(testString4).matches()).isFalse();
        assertThat(pattern.matcher(testString5).matches()).isTrue();

    }
}
