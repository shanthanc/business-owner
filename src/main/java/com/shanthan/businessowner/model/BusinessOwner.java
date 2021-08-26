package com.shanthan.businessowner.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.shanthan.businessowner.util.BusinessOwnerConstants.EMAIL_REGEX;

@Value
@Builder
@Validated
public class BusinessOwner {

    Long boNumber;
    @Size(min = 3, max = 100)
    String firstName;
    @Size(min = 2, max = 100)
    String lastName;
    @Pattern(regexp = EMAIL_REGEX, message = "Invalid Email Address")
    String emailAddress;
    @Valid
    Address address;
}
