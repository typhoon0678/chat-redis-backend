package dev.typhoon.chat_redis.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // 기본 오류 코드
    DEFAULT_BAD_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
    DEFAULT_UNAUTHORIZED(UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    DEFAULT_NOT_FOUND(NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    DEFAULT_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 다시 시도해주세요."),

    // member
    MEMBER_LOGIN_FAILED(BAD_REQUEST, "이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_EMAIL_ALREADY_EXISTS(BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    
    ;

    private final HttpStatus status;
    private final String message;

    public String getErrorCode() {
        return this.name();
    }
}
