package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.shanthan.businessowner.util.BusinessOwnerConstants.ZIPCODE_REGEX;

@Value
@Builder
@Validated
public class Address {

    @Size(min = 3, max = 500)
    String street;
    @Size(min = 1, max = 10000)
    String houseOrAptNum;
    @Size(min = 3, max = 100)
    String city;
    String state;
    @Pattern(regexp = ZIPCODE_REGEX, message = "Invalid Zipcode")
    String zipcode;
}
