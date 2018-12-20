package com.rsupport.webviewer.viewer.config.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionVO> commonError(ApiException ex, Locale locale) {
        String message = messageSource.getMessage(String.format("error.%s", String.valueOf(ex.getExceptionStatus().getCode())), null, locale);
        ExceptionVO exceptionVO = new ExceptionVO(ex.getExceptionStatus().getCode(), message);
        return new ResponseEntity<>(exceptionVO, HttpStatus.OK);
    }

    @Data
    private class ApiException extends RuntimeException {
        private final ExceptionStatus exceptionStatus;

        public ApiException(ExceptionStatus exceptionStatus) {
            super();
            this.exceptionStatus = exceptionStatus;
        }
    }

    @Data
    @AllArgsConstructor
    public class ExceptionVO {
        private Integer code;
        private String message;
    }

    @AllArgsConstructor
    @Getter
    public enum ExceptionStatus {
        INVALID_PASSWORD(2200, "invalid password"),
        INVALID_ACCESS_CODE(2100, "invalid access code"),
        NO_AVAIABLE_DEMO_HOST(2700, "no avaiable demo host"),
        DATA_NOT_FOUND(2300, "data not found"),
        INVALID_LICENSE(2400, "invalid license"),
        OVER_CONSURRENT_USER_COUNT(2500, "over cunsurrent user count"),
        OVER_SESSION(2600, "over session"),
        API_CALL_FAIL(3500, "remotecall api fail");
        private final int code;
        private final String messsage;
    }
}
