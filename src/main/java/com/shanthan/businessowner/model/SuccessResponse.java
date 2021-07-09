package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
public class SuccessResponse {

    HttpStatus httpStatus;
    String message;
}
