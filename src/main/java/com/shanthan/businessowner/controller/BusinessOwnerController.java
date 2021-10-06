package com.shanthan.businessowner.controller;

import com.shanthan.businessowner.model.AddressUpdateRequest;
import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.model.SuccessResponse;
import com.shanthan.businessowner.service.BusinessOwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.shanthan.businessowner.util.BusinessOwnerConstants.SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Slf4j
@RequestMapping("/")
@Validated
public class BusinessOwnerController {

    private final BusinessOwnerService businessOwnerService;

    public BusinessOwnerController(BusinessOwnerService businessOwnerService) {
        this.businessOwnerService = businessOwnerService;
    }

    @GetMapping("v1/business-owner/{boNumber}")
    public ResponseEntity<BusinessOwner> getBusinessOwner(@PathVariable("boNumber") Long boNumber) {
        return ok(businessOwnerService.getBusinessOwner(boNumber));
    }

    @GetMapping("v1/all-business-owners")
    public ResponseEntity<List<BusinessOwner>> getAllBusinessOwners() {
        return ok(businessOwnerService.retrieveAllBusinessOwners());
    }

    @PostMapping("v1/create-business-owner")
    public ResponseEntity<SuccessResponse> addBusinessOwner(@RequestBody BusinessOwner businessOwner) {
        businessOwnerService.addBusinessOwner(businessOwner);
        return ResponseEntity.status(CREATED).body(SuccessResponse.builder()
                .httpStatus(CREATED)
                .message(SUCCESS)
                .build());
    }

    @PutMapping("v1/update-business-owner")
    public ResponseEntity<BusinessOwner> updateBusinessOwner(@Valid @RequestBody BusinessOwner businessOwner) {
       BusinessOwner updatedBusinessOwner = businessOwnerService.updateBusinessOwner(businessOwner);
       return ResponseEntity.ok(updatedBusinessOwner);
    }

    @PatchMapping("v1/update-business-owner-name")
    public ResponseEntity<BusinessOwner> updateBusinessOwnerName(@RequestParam("boNumber") Long boNumber,
                                                                 @RequestParam("firstName") String firstName,
                                                                 @RequestParam("lastName") String lastName) {
        BusinessOwner updatedBusinessOwner =
                businessOwnerService.updateBusinessOwnerName(boNumber, firstName, lastName);
        return ResponseEntity.ok(updatedBusinessOwner);
    }

    @PatchMapping("v1/update-business-owner-address")
    public ResponseEntity<BusinessOwner> updateBusinessOwnerAddress(@RequestBody AddressUpdateRequest request) {
        BusinessOwner updatedBusinessOwner = businessOwnerService.updateBusinessOwnerAddress(request.getBoNumber(),
                request.getAddress());
        return ResponseEntity.ok(updatedBusinessOwner);
    }

    @PatchMapping("v1/update-business-owner-email")
    public ResponseEntity<BusinessOwner> updateBusinessOwnerEmail(@RequestParam("boNumber") Long boNumber,
                                                                  @RequestParam("email") String email) {
        BusinessOwner updatedBusinessOwner = businessOwnerService.updateBusinessOwnerEmailAddress(boNumber, email);
        return ResponseEntity.ok(updatedBusinessOwner);
    }

}
