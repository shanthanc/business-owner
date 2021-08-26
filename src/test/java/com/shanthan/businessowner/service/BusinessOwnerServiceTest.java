package com.shanthan.businessowner.service;

import com.google.gson.Gson;
import com.shanthan.businessowner.exception.BusinessOwnerException;
import com.shanthan.businessowner.model.Address;
import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.repository.BusinessOwnerEntity;
import com.shanthan.businessowner.repository.BusinessOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.CITY;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.EMAIL;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.FIRST_NAME;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.HOUSE_OR_APT_NUM;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.LAST_NAME;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.STATE;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.STREET;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.ZIP_CODE;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class BusinessOwnerServiceTest {

    @InjectMocks
    private BusinessOwnerService subject;

    @Mock
    private BusinessOwnerRepository businessOwnerRepository;

    private BusinessOwner businessOwner;

    private BusinessOwnerEntity businessOwnerEntity;
    private BusinessOwnerEntity updatedBusinessOwnerEntity;
    private BusinessOwnerEntity updatedAddressBusinessOwnerEntity;

    private String updatedAddressString;

    private Address updatedAddress;
    private List<BusinessOwnerEntity> businessOwnerEntityList;

    private String newFirstName;

    private String newLastName;

    private String updatedEmail;

    @BeforeEach
    void setUp() {
        openMocks(this);
        newFirstName = "newFirstName";
        newLastName = "newLastName";
        updatedEmail = "updated@someDomain.xyz";
        businessOwner = BusinessOwner.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .emailAddress(EMAIL)
                .address(Address.builder()
                        .street(STREET)
                        .houseOrAptNum(HOUSE_OR_APT_NUM)
                        .city(CITY)
                        .state(STATE.getDescription())
                        .zipcode(ZIP_CODE)
                        .build())
                .build();

        //language=JSON
        String addressString = "{\n" +
                "  \"street\": \"123 ABC St\",\n" +
                "  \"houseOrAptNum\": \"100\",\n" +
                "  \"city\": \"someCity\",\n" +
                "  \"state\": \"Illinois\",\n" +
                "  \"zipcode\": \"60601\"\n" +
                "}";

        //language=JSON
        updatedAddressString = "{\n" +
                "  \"street\": \"123 Updated St\",\n" +
                "  \"houseOrAptNum\": \"111\",\n" +
                "  \"city\": \"someUpdatedCity\",\n" +
                "  \"state\": \"Illinois\",\n" +
                "  \"zipcode\": \"60661\"\n" +
                "}";

        updatedAddress = transformToAddressObject(updatedAddressString);
        //language=JSON
        String addressString1 = "{\n" +
                "  \"street\": \"123 ABC St\",\n" +
                "  \"houseOrAptNum\": \"100\",\n" +
                "  \"city\": \"someCity1\",\n" +
                "  \"state\": \"Illinois\",\n" +
                "  \"zipcode\": \"60001\"\n" +
                "}";

        //language=JSON
        String addressString2 = "{\n" +
                "  \"street\": \"456 ABC St\",\n" +
                "  \"houseOrAptNum\": \"101\",\n" +
                "  \"city\": \"someCity2\",\n" +
                "  \"state\": \"Illinois\",\n" +
                "  \"zipcode\": \"60002\"\n" +
                "}";

        //language=JSON
        String addressString3 = "{\n" +
                "  \"street\": \"789 ABC St\",\n" +
                "  \"houseOrAptNum\": \"102\",\n" +
                "  \"city\": \"someCity3\",\n" +
                "  \"state\": \"Illinois\",\n" +
                "  \"zipcode\": \"60003\"\n" +
                "}";


        businessOwnerEntity = new BusinessOwnerEntity(10L, FIRST_NAME, LAST_NAME, EMAIL, addressString);
        updatedBusinessOwnerEntity = new BusinessOwnerEntity(10L, newFirstName, newLastName, EMAIL,
                addressString);
        updatedAddressBusinessOwnerEntity = new BusinessOwnerEntity(10L, newFirstName, newLastName, EMAIL,
                updatedAddressString);
        BusinessOwnerEntity businessOwnerEntity1 = new BusinessOwnerEntity(1L, "someFirstName1",
                "someLastName1", "test1@someDomain.xyz", addressString1);
        BusinessOwnerEntity businessOwnerEntity2 = new BusinessOwnerEntity(2L, "someFirstName2",
                "someLastName2", "test2@someDomain.xyz", addressString2);
        BusinessOwnerEntity businessOwnerEntity3 = new BusinessOwnerEntity(3L, "someFirstName3",
                "someLastName3", "test3@someDomain.xyz", addressString3);

        businessOwnerEntityList = new ArrayList<>();
        businessOwnerEntityList.add(businessOwnerEntity1);
        businessOwnerEntityList.add(businessOwnerEntity2);
        businessOwnerEntityList.add(businessOwnerEntity3);

        businessOwner = BusinessOwner.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .emailAddress(EMAIL)
                .address(Address.builder()
                        .street(STREET)
                        .city(CITY)
                        .state(STATE.getDescription())
                        .zipcode(ZIP_CODE)
                        .build())
                .build();
    }

    @Test
    @DisplayName("Test for saving new business owner in database")
    void addBusinessOwner() {
        BusinessOwnerEntity newBusinessOwnerEntity = transformToBusinessOwnerEntity(businessOwner);
        subject.addBusinessOwner(businessOwner);
        verify(businessOwnerRepository).save(newBusinessOwnerEntity);
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

    private String transformToAddressJsonString(Address address) {
        Gson gson = new Gson();
        return gson.toJson(address);
    }

    private Address transformToAddressObject(String address) {
        Gson gson = new Gson();
        return gson.fromJson(address, Address.class);
    }

    @Test
    @DisplayName("Test for retrieving business owner from database ")
    void givenValidBoNumber_whenCallMadeToReturnBusinessOwner_ReturnValidBusinessOwner() {
        when(businessOwnerRepository.getById(anyLong())).thenReturn(transformToBusinessOwnerEntity(businessOwner));
        BusinessOwner actualResult = subject.getBusinessOwner(1L);
        assertNotNull(actualResult);
        assertEquals(FIRST_NAME, actualResult.getFirstName());
        assertEquals(LAST_NAME, actualResult.getLastName());
        assertEquals(EMAIL, actualResult.getEmailAddress());
    }

    @Test
    @DisplayName("Testing when business owner doesn't exist in DB")
    void givenBoNumber_whenBusinessOwnerNotFound_throwsExceptionWithNotFoundStatus() {
        when(businessOwnerRepository.getById(anyLong())).thenThrow(new EntityNotFoundException("not found"));
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class, () ->
                subject.getBusinessOwner(1L));
        assertEquals(NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Testing when unexpected exception occurs when retrieving business owner based on boNumber")
    void givenBoNumber_whenRetrievingBusinessOwner_throwsExceptionWithInternalServerStatus() {
        when(businessOwnerRepository.getById(anyLong())).thenThrow(new RuntimeException("someException"));
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class, () ->
                subject.getBusinessOwner(1L));
        assertEquals(INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    @DisplayName("To test whether list of business owners are returned")
    void givenBusinessOwnersPresentInDB_whenRetrieveAllBusinessOwnersCalled_returnsListOfBusinessOwners() {
        when(businessOwnerRepository.findAll()).thenReturn(businessOwnerEntityList);
        List<BusinessOwner> businessOwnerList = subject.retrieveAllBusinessOwners();
        assertNotNull(businessOwnerList);
        assertEquals(3, businessOwnerList.size());
        assertEquals(1L, businessOwnerList.get(0).getBoNumber());
        assertEquals(2L, businessOwnerList.get(1).getBoNumber());
        assertEquals(3L, businessOwnerList.get(2).getBoNumber());
    }

    @Test
    @DisplayName("Testing to see if empty Business Owner List is returned when empty list is returned from db")
    void givenNoBusinessOwnersPresentInDb_whenRetrieveBusinessOwnersMethodCalled_ReturnsEmptyBusinessOwnerList() {
        when(businessOwnerRepository.findAll()).thenReturn(new ArrayList<BusinessOwnerEntity>());
        List<BusinessOwner> businessOwnerList = subject.retrieveAllBusinessOwners();
        assertNotNull(businessOwnerList);
        assertEquals(0, businessOwnerList.size());
    }

    @Test
    @DisplayName("Testing for exception when retrieving owners list")
    void givenBusinessOwnerDbIsUpAndRunning_whenRetrieveBusinessOwnersMethodCalled_ThrowsException() {
        when(businessOwnerRepository.findAll()).thenThrow(new RuntimeException("someException"));
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class, () ->
                subject.retrieveAllBusinessOwners());
        assertEquals("someException", exception.getExceptionMessage());
        assertEquals(INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Testing to see if exception is thrown when null list is returned from db")
    void givenNoBusinessOwnersPresentInDb_whenRetrieveBusinessOwnersMethodCalled_ThrowsException() {
        when(businessOwnerRepository.findAll()).thenReturn(null);
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class, () ->
                subject.retrieveAllBusinessOwners());
        assertEquals(INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Testing for update name successfully for a specific business owner")
    void givenNewNameForBusinessOwnerToUpdate_whenCalledToUpdate_thenSaveAndReturnUpdatedBusinessOwner() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(true);
        when(businessOwnerRepository.getById(anyLong())).thenReturn(businessOwnerEntity);
        when(businessOwnerRepository.save(any(BusinessOwnerEntity.class))).thenReturn(updatedBusinessOwnerEntity);
        BusinessOwner actualResult = subject.updateBusinessOwnerName(10L, newFirstName, newLastName);
        assertNotNull(actualResult);
        assertEquals(newFirstName, actualResult.getFirstName());
        assertEquals(newLastName, actualResult.getLastName());

    }

    @Test
    @DisplayName("Test to see if exception thrown when called to update business owner who doesn't exit")
    void givenNewNameForBusinessOwnerToUpdate_whenBusinessOwnerDoesNotExistInDb_thenThrowExceptionWithNotFoundStatus() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(false);
        BusinessOwnerException businessOwnerException =
                assertThrows(BusinessOwnerException.class, () ->
                        subject.updateBusinessOwnerName(10L, newFirstName, newLastName));
        assertEquals(NOT_FOUND, businessOwnerException.getHttpStatus());
    }

    @Test
    @DisplayName("Testing for update address successfully for a specific business owner")
    void givenNewAddressForBusinessOwnerToUpdate_whenCalledToUpdate_thenSaveAndReturnUpdatedBusinessOwner() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(true);
        when(businessOwnerRepository.getById(anyLong())).thenReturn(businessOwnerEntity);
        when(businessOwnerRepository.save(any(BusinessOwnerEntity.class)))
                .thenReturn(updatedAddressBusinessOwnerEntity);
        BusinessOwner actualResult = subject.updateBusinessOwnerAddress(10L, updatedAddress);
        assertNotNull(actualResult);
        assertEquals(updatedAddress, actualResult.getAddress());
    }

    @Test
    @DisplayName("Test to see if exception thrown when called to update business owner who doesn't exit")
    void givenNewAddressForBusinessOwnerToUpdate_whenDoesNotExistInDb_thenThrowExceptionWithNotFoundStatus() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(false);
        BusinessOwnerException businessOwnerException =
                assertThrows(BusinessOwnerException.class, () ->
                        subject.updateBusinessOwnerAddress(10L, updatedAddress));
        assertEquals(NOT_FOUND, businessOwnerException.getHttpStatus());
    }

    @Test
    @DisplayName("Test to check if BusinessOwner update is done")
    void givenUpdatedBusinessOwner_whenCalledForUpdate_thenSaveAndReturnUpdatedBusinessOwner() {
        updatedAddressBusinessOwnerEntity.setFirstName("fullUpdateFirstName");
        updatedAddressBusinessOwnerEntity.setLastName("fullUpdateLastName");
        updatedAddressBusinessOwnerEntity.setAddress(updatedAddressString);
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(true);
        when(businessOwnerRepository.getById(anyLong())).thenReturn(businessOwnerEntity);
        when(businessOwnerRepository.save(any(BusinessOwnerEntity.class)))
                .thenReturn(updatedAddressBusinessOwnerEntity);
        BusinessOwner actualResult = subject.updateBusinessOwner(BusinessOwner.builder()
                .boNumber(10L)
                .firstName("fullUpdateFirstName")
                .lastName("fullUpdateLastName")
                .emailAddress(EMAIL)
                .address(updatedAddress)
                .build());
        assertNotNull(actualResult);
        assertEquals("fullUpdateFirstName", actualResult.getFirstName());
        assertEquals("fullUpdateLastName", actualResult.getLastName());
    }

    @Test
    @DisplayName("Testing to see if Exception is thrown with Bad Request status")
    void givenBusinessOwnerWithoutBoNumber_whenUpdateCalled_throwNewExceptionWithBadRequestStatus() {
        BusinessOwnerException businessOwnerException =
                assertThrows(BusinessOwnerException.class, () -> subject.updateBusinessOwner(BusinessOwner.builder()
                        .firstName("fullUpdateFirstName")
                        .lastName("fullUpdateLastName")
                        .emailAddress(EMAIL)
                        .address(updatedAddress)
                        .build()));
        assertEquals(BAD_REQUEST, businessOwnerException.getHttpStatus());
    }

    @Test
    @DisplayName("Testing to see if Exception is thrown with Not Found status")
    void givenBusinessOwnerToUpdate_whenBusinessOwnerDoesNotExist_thenThrowNewExceptionWithNotFoundStatus() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(false);
        BusinessOwnerException businessOwnerException =
                assertThrows(BusinessOwnerException.class, () -> subject.updateBusinessOwner(BusinessOwner.builder()
                        .boNumber(10L)
                        .firstName("fullUpdateFirstName")
                        .lastName("fullUpdateLastName")
                        .emailAddress(EMAIL)
                        .address(updatedAddress)
                        .build()));
        assertEquals(NOT_FOUND, businessOwnerException.getHttpStatus());
    }

    @Test
    void givenBoNumberAndEmailToUpdate_whenUpdateMethodCalled_thenReturnUpdatedBusinessOwner() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(true);
        when(businessOwnerRepository.getById(anyLong())).thenReturn(businessOwnerEntity);
        BusinessOwner actualResult = subject.updateBusinessOwnerEmailAddress(10L, updatedEmail);
        assertEquals(updatedEmail, actualResult.getEmailAddress());
    }

    @Test
    void givenBoNumber_whenBusinessOwnerDoesNotExistInDb_thenThrowBusinessOwnerExceptionWithHttpStatusNotFound() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(false);
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class ,() ->
                subject.updateBusinessOwnerEmailAddress(10L, updatedEmail));
        assertEquals(NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void givenBoNumberAndNewEmailAddress_whenUpdateCalledAndExceptionThrown_thenThrowBusinessOwnerException() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(true);
        when(businessOwnerRepository.getById(anyLong())).thenThrow(new BusinessOwnerException("someException",
                FORBIDDEN));
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class ,() ->
                subject.updateBusinessOwnerEmailAddress(10L, updatedEmail));
        assertEquals(FORBIDDEN, exception.getHttpStatus());
    }

    @Test
    void givenBoNumberAndNewEmailAddress_whenUpdateCalledAndSomeExceptionThrown_thenThrowBusinessOwnerException() {
        when(businessOwnerRepository.existsById(anyLong())).thenReturn(true);
        when(businessOwnerRepository.getById(anyLong())).thenThrow(new RuntimeException("someException"));
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class ,() ->
                subject.updateBusinessOwnerEmailAddress(10L, updatedEmail));
        assertEquals(INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }
}
