package com.example.demo.chapter5.repository;

import com.example.demo.chapter5.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
