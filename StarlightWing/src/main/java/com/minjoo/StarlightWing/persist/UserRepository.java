package com.minjoo.StarlightWing.persist;

import com.minjoo.StarlightWing.dto.UserDto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDto, Long> {

    //아이디를 기준으로 회원정보를 찾기위해 사용하는 메서드
//    Optional<UserDto> findByUsername(String userNm);

    Optional<UserDto> findByUsername(String username);


    //회원가입시에 이미 존재하는 아이디인지 확인하기 위한 메서드
    boolean existsByUserid(Long userid);



}
