package com.example.demo.chapter3.repository;

import com.example.demo.chapter3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
