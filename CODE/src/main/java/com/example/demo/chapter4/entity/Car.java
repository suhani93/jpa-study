package com.example.demo.chapter4.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Car {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String modelName;

    @Column(unique = true, length = 9)
    private String licensePlateNumber;


}
