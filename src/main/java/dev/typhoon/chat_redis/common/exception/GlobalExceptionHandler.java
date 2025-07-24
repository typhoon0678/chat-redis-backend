package dev.typhoon.chat_redis.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getErrorCode(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // 서버 내부 오류
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.DEFAULT_INTERNAL_SERVER_ERROR.getErrorCode(),
                ErrorCode.DEFAULT_INTERNAL_SERVER_ERROR.getMessage());

        return ResponseEntity.status(ErrorCode.DEFAULT_INTERNAL_SERVER_ERROR.getStatus()).body(errorResponse);
    }
}
