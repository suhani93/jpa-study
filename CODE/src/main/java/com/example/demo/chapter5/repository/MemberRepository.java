package com.example.demo.chapter5.repository;

import com.example.demo.chapter5.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
