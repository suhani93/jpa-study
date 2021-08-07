package com.example.demo.chapter3.repository;

import com.example.demo.chapter3.entity.User;
import com.example.demo.chapter3.repository.UserRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Slf4j
@DataJpaTest
@Rollback(value = false)
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("영속성 컨텍스트 1차 캐시")
    @Transactional
    void test1() throws Exception{
        User user = User.builder().nickname("유저 1").build();
        userRepository.save(user);


        //select 문이 안나감
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));

        //같은 객체라고 나옴
        log.info("{} - {}",user.hashCode(), findUser.hashCode());
        assertThat(user, is(equalTo(findUser)));

    }



    @Test
    @DisplayName("영속성 컨텍스트 1차 캐시 - 영속성 컨텍스트를 날린 경우 - 1차 캐시 사용 불가 ")
    @Transactional
    void test2() throws Exception{


        User user = User.builder().nickname("유저 1").build();
        userRepository.save(user);

        em.flush();
        em.clear();


        //select 문이 나감
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));

        //다른 객체라고 나옴
        log.info("{} - {}",user.hashCode(), findUser.hashCode());
        assertThat(user, is(not(equalTo(findUser))));

    }



    @Test
    @DisplayName("영속성 컨텍스트 1차 캐시 - 트랜잭션이 다른 경우 - 1차 캐시 사용 불가")
    void test3() throws Exception{

        User user = User.builder().nickname("유저 1").build();
//        userRepository.save(user);
        //DataJpaTest 안에 @transactional 이 있어서 메소드르 빼서 propagation을  REQUIRES_NEW 로 줌

        save(user);      // 실 구현 객체의 메소드에 트랜잭션처리 되어 있음 - 트랜잭션 1

        //select 문이 나가야 함
        // 트랜잭션 처리가 없기에 그냥 다른 트랜잭선임 - 트랜잭션 2
//        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));
        User findUser = find(user.getId());

        //다른 객체라고 나옴
        log.info("{} - {}",user.hashCode(), findUser.hashCode());
        //근데 왜 영속성 컨텍스트 안에서 동작 하지?? select 문이 안나가;;;
        assertThat(user, is(not(equalTo(findUser))));


    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void save(User user){
        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    User find(Long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("not found Exception"));
    }



    @Test
    @DisplayName("영속성 컨텍스트 쓰기 지연 - save")
    @Transactional
    void test4() throws Exception{


        log.info("세이브 전");
        User user = User.builder().nickname("유저 1").build();
        userRepository.save(user);      //여기서 SAVE

        log.info("세이브 후");

        //근데 로그를 보면 세이브 전 -> 세이브 후 -> insert 문이 나감


        em.flush();
        em.clear();
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));
        assertThat(findUser, is(notNullValue()));
    }

    @Test
    @DisplayName("영속성 컨텍스트 쓰기 지연 - save 후 update")
    @Transactional
    void test5() throws Exception{


        log.info("세이브 전");
        User user = User.builder().nickname("유저 1").build();
        userRepository.save(user);      //여기서 SAVE

        log.info("세이브 후");
        log.info("업데이트 전");
        user.changeNickname("변경된 유저 1");
        log.info("업데이트 후");


        //근데 로그를 보면 세이브 전 -> 세이브 후 -> 업데이트 전 -> 업데이트 후-> insert 문이 나감

        em.flush();
        em.clear();

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));
        assertThat(findUser.getNickname(), is(equalTo("변경된 유저 1")));
    }

    @Test
    @DisplayName("영속성 컨텍스트 변경 감지")
    @Transactional
    void test6() throws Exception{


        User user = User.builder().nickname("유저 1").build();
        userRepository.save(user);      //여기서 SAVE

        user.changeNickname("변경된 유저 1");
        //변경 감지로 자동으로 업데이트가 됨

        em.flush();
        em.clear();

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));
        assertThat(findUser.getNickname(), is(equalTo("변경된 유저 1")));

    }


    @Test
    @DisplayName("영속성 컨텍스트 변경 감지 - 영속성 컨텍스트를 비우면 더티 체킹 실패")
    @Transactional
    void test7() throws Exception{


        User user = User.builder().nickname("유저 1").build();
        userRepository.save(user);      //여기서 SAVE
        em.flush();
        em.clear();
        user.changeNickname("변경된 유저 1");

        //변경 감지(더티 체킹)가 안됨
        em.flush();
        em.clear();

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("not found Exception"));
        assertThat(findUser.getNickname(), is(not(equalTo("변경된 유저 1"))));


    }

}
