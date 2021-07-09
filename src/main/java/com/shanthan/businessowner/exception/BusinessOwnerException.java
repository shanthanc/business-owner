package com.shanthan.businessowner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
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
        this.throwable = null;
        this.httpStatus = httpStatus;
    }

    public BusinessOwnerException(String exceptionMessage, HttpStatus httpStatus, Throwable throwable) {
        super(exceptionMessage, throwable);
        this.httpStatus = httpStatus;
    }
}
