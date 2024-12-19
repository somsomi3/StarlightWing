package com.minjoo.StarlightWing.persist;

import com.minjoo.StarlightWing.model.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    //아이디를 기준으로 회원정보를 찾기위해 사용하는 메서드
    Optional<MemberEntity> findByUsername(String username);

    //회원가입시에 이미 존재하는 아이디인지 확인하기 위한 메서드
    boolean existsByUsername(String username);



}
