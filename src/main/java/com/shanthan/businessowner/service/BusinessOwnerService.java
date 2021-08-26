package com.shanthan.businessowner.service;

import com.google.gson.Gson;
import com.shanthan.businessowner.exception.BusinessOwnerException;
import com.shanthan.businessowner.model.Address;
import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.model.State;
import com.shanthan.businessowner.repository.BusinessOwnerEntity;
import com.shanthan.businessowner.repository.BusinessOwnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.shanthan.businessowner.model.State.values;
import static java.util.Arrays.stream;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    private boolean isValidState(String state) {
        log.info("Validating value of state -> {} in address object... ", state);
        List<String> stateDesc = stream(values())
                .map(State::getDescription)
                .collect(toList());
        List<String> stateAbb = stream(values())
                .map(State::getAbbreviation)
                .collect(toList());
        List<String> stateStringList = Stream.of(stateDesc, stateAbb)
                .flatMap(Collection::stream)
                .collect(toList());
        return stateStringList.contains(state);
    }

    private String transformToAddressJsonString(Address address) {
        Gson gson = new Gson();
        if (!isValidState(address.getState()))
            throw new BusinessOwnerException("Invalid State ", BAD_REQUEST);
        return gson.toJson(address);
    }

    private BusinessOwnerEntity transformToBusinessOwnerEntity(BusinessOwner businessOwner) {
        BusinessOwnerEntity businessOwnerEntity = new BusinessOwnerEntity();
        businessOwnerEntity.setFirstName(of(businessOwner.getFirstName()).orElse(null));
        businessOwnerEntity.setLastName(of(businessOwner.getLastName()).orElse(null));
        businessOwnerEntity.setEmailAddress(of(businessOwner.getEmailAddress()).orElse(null));
        businessOwnerEntity.setAddress(transformToAddressJsonString(of(businessOwner.getAddress())
                .orElse(null)));
        return businessOwnerEntity;
    }

    private BusinessOwner transformToBusinessOwner(BusinessOwnerEntity businessOwnerEntity) {
        return BusinessOwner.builder()
                .boNumber(businessOwnerEntity.getBoNumber())
                .firstName(of(businessOwnerEntity.getFirstName()).orElse(null))
                .lastName(of(businessOwnerEntity.getLastName()).orElse(null))
                .emailAddress(of(businessOwnerEntity.getEmailAddress()).orElse(null))
                .address(transformToAddressObject(of(businessOwnerEntity.getAddress()).orElse(null)))
                .build();
    }

    public void addBusinessOwner(BusinessOwner businessOwner) {
        log.info("Saving new business owner in database... ");
        try {
            businessOwnerRepository.save(transformToBusinessOwnerEntity(businessOwner));
        } catch (BusinessOwnerException bex) {
            log.error(bex.getExceptionMessage(), bex);
            throw new BusinessOwnerException(bex.getExceptionMessage(), bex.getHttpStatus());
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
        } catch (Exception ex) {
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
                        .emailAddress(x.getEmailAddress())
                        .address(transformToAddressObject(x.getAddress()))
                        .build()));
            }
            return businessOwnerList;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving business owner list from database ", ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
    }

    public BusinessOwner updateBusinessOwnerName(Long boNumber, String firstName, String lastName) {
        BusinessOwner businessOwner;
        try {
            log.info("Checking if business owner with boNumber -> {} is present in the database ", boNumber);
            if (businessOwnerRepository.existsById(boNumber)) {
                BusinessOwnerEntity businessOwnerEntity = businessOwnerRepository.getById(boNumber);
                log.info("Updating the name of business owner with boNumber -> {} ", boNumber);
                businessOwnerEntity.setFirstName(firstName);
                businessOwnerEntity.setLastName(lastName);
                log.info("Saving business owner with boNumber -> {} with updated name ", boNumber);
                businessOwnerEntity = businessOwnerRepository.save(businessOwnerEntity);
                businessOwner = transformToBusinessOwner(businessOwnerEntity);
            } else {
                throw new BusinessOwnerException("Business owner with boNumber -> " + boNumber + " not found ",
                        NOT_FOUND);
            }
            return businessOwner;
        } catch (BusinessOwnerException ex) {
            log.error(ex.getMessage(), ex);
            throw new BusinessOwnerException(ex.getExceptionMessage(), ex.getHttpStatus(), ex);
        } catch (Exception ex) {
            log.error("Unable to update Business owner with boNumber -> {} ", boNumber, ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
    }

    public BusinessOwner updateBusinessOwnerAddress(Long boNumber, Address address) {
        BusinessOwner businessOwner;
        try {
            if (businessOwnerRepository.existsById(boNumber)) {
                String addressString = transformToAddressJsonString(address);
                BusinessOwnerEntity businessOwnerEntity = businessOwnerRepository.getById(boNumber);
                businessOwnerEntity.setAddress(addressString);
                businessOwnerEntity = businessOwnerRepository.save(businessOwnerEntity);
                businessOwner = transformToBusinessOwner(businessOwnerEntity);
            } else {
                throw new BusinessOwnerException("Business owner with boNumber -> " + boNumber + " not found ",
                        NOT_FOUND);
            }
        } catch (BusinessOwnerException ex) {
            log.error(ex.getMessage(), ex);
            throw new BusinessOwnerException(ex.getExceptionMessage(), ex.getHttpStatus(), ex);
        } catch (Exception ex) {
            log.error("Unable to update Business owner with boNumber -> {} ", boNumber, ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
        return businessOwner;
    }

    public BusinessOwner updateBusinessOwner(BusinessOwner businessOwner) {
        if (ObjectUtils.isEmpty(businessOwner.getBoNumber())) {
            throw new BusinessOwnerException("boNumber must be a valid number ", BAD_REQUEST);
        }
        long boNumber = businessOwner.getBoNumber();
        try {
            if (businessOwnerRepository.existsById(boNumber)) {
                BusinessOwnerEntity businessOwnerEntity = businessOwnerRepository.getById(boNumber);
                businessOwnerEntity.setFirstName(businessOwner.getFirstName());
                businessOwnerEntity.setLastName(businessOwner.getLastName());
                businessOwnerEntity.setAddress(transformToAddressJsonString(businessOwner.getAddress()));
                businessOwnerEntity = businessOwnerRepository.save(businessOwnerEntity);
                businessOwner = transformToBusinessOwner(businessOwnerEntity);
            } else {
                throw new BusinessOwnerException("Business Owner with boNumber -> " + boNumber + " not found",
                        NOT_FOUND);
            }
        } catch (BusinessOwnerException boe) {
            log.error(boe.getExceptionMessage(), boe);
            throw new BusinessOwnerException(boe.getExceptionMessage(), boe.getHttpStatus(), boe);
        } catch (Exception ex) {
            log.error("Unable to update business owner with boNumber -> {} ", boNumber);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
        return businessOwner;
    }

    public BusinessOwner updateBusinessOwnerEmailAddress(Long boNumber, String emailAddress) {
        if(!businessOwnerRepository.existsById(boNumber)) {
            throw new BusinessOwnerException("Business Owner with boNumber " + boNumber + "does not exist ",
                    NOT_FOUND);
        }
        try {
            BusinessOwnerEntity businessOwnerEntity = businessOwnerRepository.getById(boNumber);
            log.info("Updating Business Owner email with boNumber {} ", boNumber);
            businessOwnerEntity.setEmailAddress(emailAddress);
            return transformToBusinessOwner(businessOwnerEntity);
        } catch (BusinessOwnerException bex) {
            log.error("Error when updating Business Owner with boNumber {} ", boNumber, bex);
            throw new BusinessOwnerException(bex.getExceptionMessage(), bex.getHttpStatus(), bex);
        }
        catch (Exception ex) {
            log.error("Error when updating Business Owner with boNumber {} ", boNumber, ex);
            throw new BusinessOwnerException(ex.getMessage(), INTERNAL_SERVER_ERROR, ex);
        }
    }
}

