package com.shanthan.businessowner.controller;

import com.shanthan.businessowner.model.Address;
import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.model.SuccessResponse;
import com.shanthan.businessowner.service.BusinessOwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.shanthan.businessowner.model.State.ILLINOIS;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.*;
import static com.shanthan.businessowner.util.BusinessOwnerConstants.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

class BusinessOwnerControllerTest {

    @InjectMocks
    private BusinessOwnerController subject;

    @Mock
    private BusinessOwnerService businessOwnerService;

    private BusinessOwner businessOwner;

    private BusinessOwner updatedBusinessOwner;

    private List<BusinessOwner> businessOwnerList;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        businessOwner = BusinessOwner.builder()
                .boNumber(1L)
                .firstName("someFirstName")
                .lastName("someLastName")
                .address(Address.builder()
                        .street("123 ABC St")
                        .houseOrAptNum("100")
                        .city("someCity")
                        .state(ILLINOIS.getDescription())
                        .zipcode("600001")
                        .build())
                .build();

       BusinessOwner businessOwner1 = BusinessOwner.builder()
                .boNumber(2L)
                .firstName("someFirstName2")
                .lastName("someLastName2")
                .address(Address.builder()
                        .street("223 ABC St")
                        .houseOrAptNum("101")
                        .city("someCity2")
                        .state(ILLINOIS.getDescription())
                        .zipcode("600011")
                        .build())
                .build();

        BusinessOwner businessOwner2 = BusinessOwner.builder()
                .boNumber(3L)
                .firstName("someFirstName3")
                .lastName("someLastName3")
                .address(Address.builder()
                        .street("323 ABC St")
                        .houseOrAptNum("201")
                        .city("someCity3")
                        .state(ILLINOIS.getDescription())
                        .zipcode("600013")
                        .build())
                .build();

        BusinessOwner businessOwner3 = BusinessOwner.builder()
                .boNumber(4L)
                .firstName("someFirstName4")
                .lastName("someLastName4")
                .address(Address.builder()
                        .street("423 ABC St")
                        .houseOrAptNum("401")
                        .city("someCity4")
                        .state(ILLINOIS.getDescription())
                        .zipcode("600014")
                        .build())
                .build();

        businessOwnerList = new ArrayList<>();
        businessOwnerList.add(businessOwner1);
        businessOwnerList.add(businessOwner2);
        businessOwnerList.add(businessOwner3);

        updatedBusinessOwner = BusinessOwner.builder()
                .boNumber(BO_NUMBER)
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .address(Address.builder()
                        .street(UPDATED_STREET)
                        .houseOrAptNum(UPDATED_HOUSE_OR_APT_NUM)
                        .city(UPDATED_CITY)
                        .state(ILLINOIS.getDescription())
                        .zipcode(UPDATED_ZIP_CODE)
                        .build())
                .build();
    }

    @Test
    @DisplayName("Test for returning a specific business owner when boNumber is given")
    void givenValidBoNumber_whenGetBusinessOwnerCalled_thenReturnBusinessOwnerResponse() {
        when(businessOwnerService.getBusinessOwner(anyLong())).thenReturn(businessOwner);
        ResponseEntity<BusinessOwner> actualResponse = subject.getBusinessOwner(1L);
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getBody());
        assertEquals(OK, actualResponse.getStatusCode());
        assertEquals("someFirstName", actualResponse.getBody().getFirstName());
    }

    @Test
    @DisplayName("Test for returning a list of all business owners")
    void givenValidBusinessOwnersPresentInDb_whenCallMadeToRetrieveListOfAllBusinessOwners_thenReturnListOfBOwners() {
        when(businessOwnerService.retrieveAllBusinessOwners()).thenReturn(businessOwnerList);
        ResponseEntity<List<BusinessOwner>> actualResponse = subject.getAllBusinessOwners();
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getBody());
        assertEquals(OK, actualResponse.getStatusCode());
        assertEquals(3, actualResponse.getBody().size());
        assertEquals("someFirstName3", actualResponse.getBody().get(1).getFirstName());
    }

    @Test
    @DisplayName("Test for saving a new business owner in database")
    void givenValidBusinessOwnerRequest_whenCallMadeToAddBusinessOwnerToDb_thenReturnStatus201CreatedWithSuccessMesg() {
        doNothing().when(businessOwnerService).addBusinessOwner(any(BusinessOwner.class));
        ResponseEntity<SuccessResponse> actualResponse = subject.addBusinessOwner(businessOwner);
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getBody());
        assertEquals(CREATED, actualResponse.getStatusCode());
        assertEquals(SUCCESS, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("Test for updating an existing business owner in database")
    void givenValidBusinessOwnerRequest_whenUpdateCallMade_thenReturn200StatusCodeWithUpdatedBusinessOwner() {
        when(businessOwnerService.updateBusinessOwner(any(BusinessOwner.class))).thenReturn(updatedBusinessOwner);
        ResponseEntity<BusinessOwner> actualResult = subject.updateBusinessOwner(updatedBusinessOwner);
        assertNotNull(actualResult);
        assertEquals(OK, actualResult.getStatusCode());
        assertNotNull(actualResult.getBody());
    }

    @Test
    void givenValidBusinessOwnerNameToUpdate_whenUpdateCallMade_thenReturn200StatusCodeWithUpdatedBusinessOwner() {
        BusinessOwner nameUpdatedBo = BusinessOwner.builder()
                .boNumber(BO_NUMBER)
                .firstName(FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .address(Address.builder()
                        .street(STREET)
                        .houseOrAptNum(HOUSE_OR_APT_NUM)
                        .city(CITY)
                        .state(ILLINOIS.getDescription())
                        .zipcode(ZIP_CODE)
                        .build())
                .build();
        when(businessOwnerService.updateBusinessOwnerName(anyLong(),anyString(),anyString())).thenReturn(nameUpdatedBo);
        ResponseEntity<BusinessOwner> actualResult =
                subject.updateBusinessOwnerName(1L, UPDATED_FIRST_NAME, UPDATED_LAST_NAME);
        assertNotNull(actualResult);
        assertNotNull(actualResult.getBody());
        assertEquals(OK, actualResult.getStatusCode());
    }

    @Test
    void givenValidBusinessOwnerAddressToUpdate_whenUpdateCallMade_thenReturn200StatusCodeWithUpdatedBusinessOwner() {
        BusinessOwner nameUpdatedBo = BusinessOwner.builder()
                .boNumber(BO_NUMBER)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .address(Address.builder()
                        .street(UPDATED_STREET)
                        .houseOrAptNum(UPDATED_HOUSE_OR_APT_NUM)
                        .city(UPDATED_CITY)
                        .state(ILLINOIS.getDescription())
                        .zipcode(UPDATED_ZIP_CODE)
                        .build())
                .build();
        when(businessOwnerService.updateBusinessOwnerName(anyLong(),anyString(),anyString())).thenReturn(nameUpdatedBo);
        ResponseEntity<BusinessOwner> actualResult =
                subject.updateBusinessOwnerName(1L, UPDATED_FIRST_NAME, UPDATED_LAST_NAME);
        assertNotNull(actualResult);
        assertNotNull(actualResult.getBody());
        assertEquals(OK, actualResult.getStatusCode());
    }

}