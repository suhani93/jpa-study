package com.example.demo.chapter4.repository;

import com.example.demo.chapter4.entity.Car;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Transactional
@Rollback(value = false)
@Slf4j
class CarRepositoryTest {

    @Autowired
    CarRepository carRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("유니크 제약 조건 테스트")
    void test1() throws Exception{
        Car car = Car.builder().licensePlateNumber("1234-1234").modelName("Mercedes-Benz S-Class").build();
        Car car2 = Car.builder().licensePlateNumber("1234-1234").modelName("Mercedes-Benz C-Class").build();
        carRepository.save(car);

        assertThrows(Exception.class, () -> {
            carRepository.save(car2);
        });
    }


    @Test
    @DisplayName("널 제약 조건 테스트")
    void test2() throws Exception{
        Car car = Car.builder().licensePlateNumber("1234-1234").modelName(null).build();
        assertThrows(Exception.class, () -> {
            carRepository.save(car);
        });
    }



    @Test
    @DisplayName("길이 제약 조건 테스트")
    void test3() throws Exception{
        Car car = Car.builder().licensePlateNumber("1234-123456").modelName("Mercedes-Benz S-Class").build();
        assertThrows(Exception.class, () -> {
            carRepository.save(car);
        });
    }



}