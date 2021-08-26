package com.shanthan.businessowner.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@Sql(scripts = "classpath:db/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
@ActiveProfiles("test")
class BusinessOwnerRepositoryTest {

    @Autowired
    BusinessOwnerRepository subject;

    @BeforeEach
    void setUp() {
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql")
    void findAll() {
        List<BusinessOwnerEntity> businessOwnerEntityList = subject.findAll();
        assertNotNull(businessOwnerEntityList);
        assertEquals(3, businessOwnerEntityList.size());
        assertEquals(1L, businessOwnerEntityList.get(0).getBoNumber());
        assertEquals("someFirstName1", businessOwnerEntityList.get(0).getFirstName());
        assertEquals("test1@someDomain.xyz", businessOwnerEntityList.get(0).getEmailAddress());

    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql")
    void getById() {
        BusinessOwnerEntity businessOwnerEntity = subject.getById(2L);
        assertNotNull(businessOwnerEntity);
        assertEquals("someFirstName2", businessOwnerEntity.getFirstName());
        assertEquals("someLastName2", businessOwnerEntity.getLastName());
        assertEquals("test2@someDomain.xyz", businessOwnerEntity.getEmailAddress());
    }
}