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

import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.BO_NUMBER;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.CITY;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class BusinessOwnerServiceTest {

    @InjectMocks
    private BusinessOwnerService subject;

    @Mock
    private BusinessOwnerRepository businessOwnerRepository;

    private BusinessOwner businessOwner;

    private BusinessOwnerEntity businessOwnerEntity1;
    private BusinessOwnerEntity businessOwnerEntity2;
    private BusinessOwnerEntity businessOwnerEntity3;

    private List<BusinessOwnerEntity> businessOwnerEntityList;

    @BeforeEach
    void setUp() {
        openMocks(this);
        businessOwner = BusinessOwner.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .address(Address.builder()
                        .street(STREET)
                        .houseOrAptNum(HOUSE_OR_APT_NUM)
                        .city(CITY)
                        .state(STATE)
                        .zipcode(ZIP_CODE)
                        .build())
                .build();
        //language=JSON
        String addressString1 = "{\n" +
                "  \"street\": \"123 ABC St\",\n" +
                "  \"houseOrAptNum\": \"100\",\n" +
                "  \"city\": \"someCity1\",\n" +
                "  \"State\": \"IL\",\n" +
                "  \"zipcode\": \"600001\"\n" +
                "}";

        //language=JSON
        String addressString2 = "{\n" +
                "  \"street\": \"456 ABC St\",\n" +
                "  \"houseOrAptNum\": \"101\",\n" +
                "  \"city\": \"someCity2\",\n" +
                "  \"State\": \"IL\",\n" +
                "  \"zipcode\": \"600002\"\n" +
                "}";

        //language=JSON
        String addressString3 = "{\n" +
                "  \"street\": \"789 ABC St\",\n" +
                "  \"houseOrAptNum\": \"102\",\n" +
                "  \"city\": \"someCity3\",\n" +
                "  \"State\": \"IL\",\n" +
                "  \"zipcode\": \"600003\"\n" +
                "}";


        businessOwnerEntity1 = new BusinessOwnerEntity(1L, "someFirstName1",
                "someLastName1", addressString1);
        businessOwnerEntity2 = new BusinessOwnerEntity(2L, "someFirstName2",
                "someLastName2", addressString2);
        businessOwnerEntity3 = new BusinessOwnerEntity(3L, "someFirstName3",
                "someLastName3", addressString3);

        businessOwnerEntityList = new ArrayList<>();
        businessOwnerEntityList.add(businessOwnerEntity1);
        businessOwnerEntityList.add(businessOwnerEntity2);
        businessOwnerEntityList.add(businessOwnerEntity3);

        businessOwner = BusinessOwner.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .address(Address.builder()
                        .street(STREET)
                        .city(CITY)
                        .state(STATE)
                        .zipcode(ZIP_CODE)
                        .build())
                .build();
    }

    @Test
    @DisplayName("Test for saving new business owner in database")
    void addBusinessOwner() {
        BusinessOwnerEntity newBusinessOwnerEntity =  transformToBusinessOwnerEntity(businessOwner);
        subject.addBusinessOwner(businessOwner);
        verify(businessOwnerRepository).save(newBusinessOwnerEntity);
    }

    private BusinessOwnerEntity transformToBusinessOwnerEntity(BusinessOwner businessOwner) {
        BusinessOwnerEntity businessOwnerEntity = new BusinessOwnerEntity();
        businessOwnerEntity.setFirstName(of(businessOwner.getFirstName()).orElse(null));
        businessOwnerEntity.setLastName(of(businessOwner.getLastName()).orElse(null));
        businessOwnerEntity.setAddress(transformToAddressJsonString(of(businessOwner.getAddress())
                .orElse(null)));
        return businessOwnerEntity;
    }

    private String transformToAddressJsonString(Address address) {
        Gson gson = new Gson();
        return gson.toJson(address);
    }

    @Test
    @DisplayName("Test for retrieving business owner from database ")
    void givenValidBoNumber_whenCallMadeToReturnBusinessOwner_ReturnValidBusinessOwner() {
        when(businessOwnerRepository.getById(anyLong())).thenReturn(transformToBusinessOwnerEntity(businessOwner));
        BusinessOwner actualResult = subject.getBusinessOwner(1L);
        assertNotNull(actualResult);
        assertEquals(FIRST_NAME, actualResult.getFirstName());
        assertEquals(LAST_NAME, actualResult.getLastName());
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
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class, () -> subject.retrieveAllBusinessOwners());
        assertEquals("someException", exception.getExceptionMessage());
        assertEquals(INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Testing to see if exception is thrown when null list is returned from db")
    void givenNoBusinessOwnersPresentInDb_whenRetrieveBusinessOwnersMethodCalled_ThrowsException() {
        when(businessOwnerRepository.findAll()).thenReturn(null);
        BusinessOwnerException exception = assertThrows(BusinessOwnerException.class, () -> subject.retrieveAllBusinessOwners());
        assertEquals(INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }
}