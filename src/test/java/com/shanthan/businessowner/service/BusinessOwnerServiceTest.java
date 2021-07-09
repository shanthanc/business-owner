package com.shanthan.businessowner.service;

import com.shanthan.businessowner.model.Address;
import com.shanthan.businessowner.model.BusinessOwner;
import com.shanthan.businessowner.repository.BusinessOwnerEntity;
import com.shanthan.businessowner.repository.BusinessOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

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

    }

    @Test
    @Disabled
    void addBusinessOwner() {
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
    void givenNoBusinessOwnersPresentInDb_whenRetrieveBusinessOwnersMethodCalled_ReturnsEmptyBusinessOwnerList() {
        when(businessOwnerRepository.findAll()).thenReturn(businessOwnerEntityList);
        List<BusinessOwner> businessOwnerList = subject.retrieveAllBusinessOwners();
        assertNotNull(businessOwnerList);
    }
}