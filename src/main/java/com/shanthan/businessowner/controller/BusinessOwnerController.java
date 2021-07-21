package com.shanthan.businessowner.controller;

import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.model.SuccessResponse;
import com.shanthan.businessowner.service.BusinessOwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shanthan.businessowner.util.BusinessOwnerConstants.SUCCESS;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@Slf4j
@RequestMapping("/v1")
public class BusinessOwnerController {

    private final BusinessOwnerService businessOwnerService;

    public BusinessOwnerController(BusinessOwnerService businessOwnerService) {
        this.businessOwnerService = businessOwnerService;
    }

    @GetMapping("/business-owner/{boNumber}")
    public ResponseEntity<BusinessOwner> getBusinessOwner(@PathVariable Long boBumber) {
        return ok(businessOwnerService.getBusinessOwner(boBumber));
    }

    @GetMapping("/business-owners")
    public ResponseEntity<List<BusinessOwner>> getAllBusinessOwners() {
        return ok(businessOwnerService.retrieveAllBusinessOwners());
    }

    @PostMapping("/create-business-owner")
    public ResponseEntity<SuccessResponse> addBusinessOwner(@RequestBody BusinessOwner businessOwner) {
       return ResponseEntity.status(CREATED).body(SuccessResponse.builder()
               .httpStatus(CREATED)
               .message(SUCCESS)
               .build());
    }
}
