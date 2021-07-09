package com.shanthan.businessowner.repository;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Data
@EqualsAndHashCode
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BusinessOwnerEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    private long boNumber;
    private String firstName;
    private String lastName;
    private String address;
}

