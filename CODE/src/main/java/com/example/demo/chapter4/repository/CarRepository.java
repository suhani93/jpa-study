package com.example.demo.chapter4.repository;

import com.example.demo.chapter4.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,Long> {

}
