package com.shanthan.businessowner.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BusinessOwnerException extends RuntimeException {

    private String exceptionMessage;
    private HttpStatus httpStatus;
    private Throwable throwable;

    public BusinessOwnerException() {
        super();
        this.exceptionMessage = null;
        this.httpStatus = null;
        this.throwable = null;
    }

    public BusinessOwnerException(String exceptionMessage, HttpStatus httpStatus) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
        this.throwable = null;
        this.httpStatus = httpStatus;
    }

    public BusinessOwnerException(String exceptionMessage, HttpStatus httpStatus, Throwable throwable) {
        super(exceptionMessage, throwable);
        this.exceptionMessage = exceptionMessage;
        this.httpStatus = httpStatus;
        this.throwable = throwable;
    }
}
