package com.minjoo.StarlightWing.service;

import com.minjoo.StarlightWing.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j// 로그를 쓸수 있게끔함
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

//       패스워드는 암호화해서 db에 넣기
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;// 좀전에 구현한 member repository를 멤버번수로 만들어주자
    @Override
//    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {// 스프링 시큐리티에서 제공하는 기능을 사용하기위해서
        //이 메서드가 구현되어있어야 함.
        return this.memberRepository.findByUsername(username)// 생성된 메서드 구현. 찾아보고
            .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> "+ username));// 만약에 없으면 UsernameNotFoundException을 발생시키자

    }

    //회원가입
    public MemberEntity register(Auth.SignUp member){
        //중복된 아이디 확인
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if (exists){// 만약에 존재한다면
            throw new RuntimeException("이미 사용중인 아이디 입니다.");
        }
        //비밀번호 암호화하기
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());//회원정보를 저장
        return result;

    }

    //로그인시 검증을 위한 메서드
    public MemberEntity authenticate(Auth.SignIn member) {
        var user = this.memberRepository.findByUsername(member.getUsername())
            .orElseThrow(()-> new RuntimeException("존재하지 않는 아이디"));
        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니당");
        }

        return user;
    }
}
