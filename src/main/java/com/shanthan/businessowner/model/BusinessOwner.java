package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BusinessOwner {

    long boNumber;
    String firstName;
    String lastName;
    Address address;
}
