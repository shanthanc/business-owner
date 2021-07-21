package com.shanthan.businessowner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessOwnerRepository extends JpaRepository<BusinessOwnerEntity, Long> {

    @Override
    List<BusinessOwnerEntity> findAll();

    @Override
    BusinessOwnerEntity getById(Long boNumber);

}
