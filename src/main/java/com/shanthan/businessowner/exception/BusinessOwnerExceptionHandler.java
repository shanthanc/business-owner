package com.shanthan.businessowner.exception;

import com.shanthan.businessowner.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

import static com.shanthan.businessowner.util.BusinessOwnerConstants.FIELD_NAME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class BusinessOwnerExceptionHandler {

    @ExceptionHandler(BusinessOwnerException.class)
    public ResponseEntity<ErrorResponse> handleBusinessOwnerException(BusinessOwnerException exception) {
        log.error("Exception occurred -> {}", exception.getExceptionMessage(), exception);
        return ResponseEntity.status(exception.getHttpStatus())
                .body(ErrorResponse.builder()
                        .exceptionMessage(exception.getExceptionMessage())
                        .httpStatus(exception.getHttpStatus())
                        .build());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingPathVariableException(MissingPathVariableException exception) {
        log.error("Exception occurred -> {} ", exception.getMessage(), exception);
        Map<String, String> errorFields = new HashMap<>();
        errorFields.put(FIELD_NAME, exception.getVariableName());
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .httpStatus(BAD_REQUEST)
                        .exceptionMessage(exception.getMessage())
                        .errorFields(errorFields)
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.error("Exception occurred -> {} ", exception.getMessage(), exception);
        Map<String, String> errorFields = new HashMap<>();
        errorFields.put(FIELD_NAME, exception.getName());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .exceptionMessage(exception.getMessage())
                        .httpStatus(BAD_REQUEST)
                        .errorFields(errorFields)
                        .build());
    }
}
