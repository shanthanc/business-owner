package com.shanthan.businessowner.service;

import com.google.gson.Gson;
import com.shanthan.businessowner.exception.BusinessOwnerException;
import com.shanthan.businessowner.model.Address;
import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.repository.BusinessOwnerEntity;
import com.shanthan.businessowner.repository.BusinessOwnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.of;
import static org.springframework.http.HttpStatus.*;

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

    private BusinessOwnerEntity transformToBusinessOwnerEntity(BusinessOwner businessOwner) {
        BusinessOwnerEntity businessOwnerEntity = new BusinessOwnerEntity();
        businessOwnerEntity.setFirstName(of(businessOwner.getFirstName()).orElse(null));
        businessOwnerEntity.setLastName(of(businessOwner.getLastName()).orElse(null));
        businessOwnerEntity.setAddress(transformToAddressJsonString(of(businessOwner.getAddress())
                .orElse(null)));
        return businessOwnerEntity;
    }

    private BusinessOwner transformToBusinessOwner(BusinessOwnerEntity businessOwnerEntity) {
        return BusinessOwner.builder()
                .firstName(of(businessOwnerEntity.getFirstName()).orElse(null))
                .lastName(of(businessOwnerEntity.getLastName()).orElse(null))
                .address(transformToAddressObject(of(businessOwnerEntity.getAddress()).orElse(null)))
                .build();
    }

    public void addBusinessOwner(BusinessOwner businessOwner) {
        log.info("Saving new business owner in database... ");
        try {
            businessOwnerRepository.save(transformToBusinessOwnerEntity(businessOwner));
        } catch (Exception ex) {
            log.error("Exception occurred while adding new business owner", ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
    }

    public BusinessOwner getBusinessOwner(Long boNumber) {
        log.info("Retrieving business owner with boNumber -> {} from database ", boNumber);
        try {
            BusinessOwnerEntity businessOwnerEntity = businessOwnerRepository.getById(boNumber);
            return transformToBusinessOwner(businessOwnerEntity);
        } catch (EntityNotFoundException entityNotFoundException) {
            log.error("Business owner with boNumber -> {} doesn't exist ", boNumber, entityNotFoundException);
            throw new BusinessOwnerException(entityNotFoundException.getMessage(), NOT_FOUND, entityNotFoundException);
        }
        catch (Exception ex) {
            log.error("Exception occurred while retrieving business owner with boNumber -> {} from database ",
                    boNumber, ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
    }

    public List<BusinessOwner> retrieveAllBusinessOwners() {
        log.info("Retrieving all business owners present... ");
        try {
            List<BusinessOwner> businessOwnerList = new ArrayList<>();
            List<BusinessOwnerEntity> businessOwnerEntityList = of(businessOwnerRepository.findAll())
                    .orElse(null);
            if (!businessOwnerEntityList.isEmpty()) {
                businessOwnerEntityList.forEach(x -> businessOwnerList.add(BusinessOwner.builder()
                        .boNumber(x.getBoNumber())
                        .firstName(x.getFirstName())
                        .lastName(x.getLastName())
                        .address(transformToAddressObject(x.getAddress()))
                        .build()));
            }
            return businessOwnerList;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving business owner list from database ", ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
    }
}
