package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddressUpdateRequest {

    long boNumber;
    Address address;
}
