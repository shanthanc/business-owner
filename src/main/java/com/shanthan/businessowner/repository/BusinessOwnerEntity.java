package com.shanthan.businessowner.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@ToString
@RequiredArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "business_owner")
public class BusinessOwnerEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long boNumber;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BusinessOwnerEntity that = (BusinessOwnerEntity) o;

        return Objects.equals(boNumber, that.boNumber);
    }

    @Override
    public int hashCode() {
        return boNumber.hashCode() +
                firstName.hashCode() +
                lastName.hashCode() +
                emailAddress.hashCode() +
                address.hashCode();
    }
}

