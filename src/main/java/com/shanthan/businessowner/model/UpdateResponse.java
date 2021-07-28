package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateResponse {

    long boNumber;
    String firstName;
    String lastName;
    String message;
}
