package com.minjoo.StarlightWing.model;

import java.util.List;
import lombok.Data;

public class Auth {
    @Data
    public static class SignIn{
        //로그인할때 필요한 정보 username과 passsword

        private String username;
        private String password;
    }
    @Data
    public static class SignUp {
        //회원가입할때 필요한 정보 username과 passsword
        private String username;
        private String password;

        private String name;


        //어떤 권한을 부여할지_ 내부 로직으로 처리
        //일반회원: 일반회원 , 관리자: 추가내용추가됨
        private List<String> roles;


        //회원가입의 내용을 memberentity로 바꿀수 있도록 메서드를 하나 만들어줌
        public MemberEntity toEntity() {
            //memberentity에 Builder어노테이션을붙여주고 와서 아래에서 사용하자.
            return MemberEntity.builder()
                        .username(this.username)
                        .password(this.password)
                        .name(this.name)

                        .roles(this.roles)
                        .build();
        }
    }
}
