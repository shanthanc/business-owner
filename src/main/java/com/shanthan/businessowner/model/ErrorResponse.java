package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class ErrorResponse {

    String statusMessage;
    String exceptionMessage;
    HttpStatus httpStatus;
    Map<String, List<String>> errorFields;
}
