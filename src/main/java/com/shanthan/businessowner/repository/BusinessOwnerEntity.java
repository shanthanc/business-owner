package com.shanthan.businessowner.repository;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "business_owner")
public class BusinessOwnerEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long boNumber;
    private String firstName;
    private String lastName;
    private String address;
}

