package com.shanthan.businessowner.service;

import com.google.gson.Gson;
import com.shanthan.businessowner.exception.BusinessOwnerException;
import com.shanthan.businessowner.model.Address;
import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.repository.BusinessOwnerEntity;
import com.shanthan.businessowner.repository.BusinessOwnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class BusinessOwnerService {

    private final BusinessOwnerRepository businessOwnerRepository;

    public BusinessOwnerService(BusinessOwnerRepository businessOwnerRepository) {
        this.businessOwnerRepository = businessOwnerRepository;
    }

    private Address transformToAddressObject(String address) {
        Gson gson = new Gson();
        return gson.fromJson(address, Address.class);
    }

    private String transformToAddressJsonString(Address address) {
        Gson gson = new Gson();
        return gson.toJson(address);
    }
    private void transformToBusinessOwnerEntity(BusinessOwner businessOwner) {
        BusinessOwnerEntity businessOwnerEntity = new BusinessOwnerEntity();
        businessOwnerEntity.setFirstName(businessOwner.getFirstName());
        businessOwnerEntity.setLastName(businessOwner.getLastName());
        businessOwnerEntity.setAddress(transformToAddressJsonString(businessOwner.getAddress()));
    }
    public void addBusinessOwner(BusinessOwner businessOwner) {

    }

    public List<BusinessOwner> retrieveAllBusinessOwners() {
        log.info("Retrieving all business owners present... ");
        try {
            List<BusinessOwnerEntity> businessOwnerEntityList = businessOwnerRepository.findAll();
            List<BusinessOwner> businessOwnerList = new ArrayList<>();
            businessOwnerEntityList.forEach(boel -> businessOwnerList.add(BusinessOwner.builder()
                    .boNumber(boel.getBoNumber())
                    .firstName(boel.getFirstName())
                    .lastName(boel.getLastName())
                    .address(transformToAddressObject(boel.getAddress()))
                    .build()));
            return businessOwnerList;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving business owner list from database ", ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
    }
}
