package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Address {

    String street;
    String houseOrAptNum;
    String city;
    State state;
    String zipcode;
}
