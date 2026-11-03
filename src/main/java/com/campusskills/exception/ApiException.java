package com.campusskills.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ApiException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.code = null;
    }

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = null;
    }

    public ApiException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
