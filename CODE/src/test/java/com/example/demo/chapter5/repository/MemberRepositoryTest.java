package com.example.demo.chapter5.repository;

import com.example.demo.chapter5.entity.Member;
import com.example.demo.chapter5.entity.Team;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback(value = false)
@Transactional
@Slf4j
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("단방향 매핑")
    void test1() throws Exception {

        Team team = Team.builder().name("팀 1").build();
        teamRepository.save(team);

        Member member = Member.builder().name("멤버 1").team(team).build();
        memberRepository.save(member);

        em.flush();
        em.clear();

        Team findTeam = teamRepository.findById(team.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));

        log.info("findTeam - {}",findTeam);
        log.info("findMember - {}",findMember);

        assertThat(findTeam, is(notNullValue()));
        assertThat(findTeam.getId(), is(equalTo(team.getId())));
        assertThat(findMember, is(notNullValue()));
        assertThat(findMember.getId(), is(equalTo(member.getId())));
        assertThat(findMember.getTeam(), is(equalTo(findTeam)));

    }

}