package com.minjoo.StarlightWing.dto;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "member")
public class UserDto implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid", nullable = false, unique = true)
    private Long userid;

    //아이디로 사용할 유저네임과 비밀번호
    private String username;
    private String password;

    // 사용자의 본명***
    private String name;  // 추가된 필드



    @Builder(toBuilder = true)
    private UserDto(Long userid, String username, String password) {
        this.userid = userid;
        this.password = password;
        this.username = username;
    }


    @Override
    public String getUsername() {
        return this.username;
    }

    //아까 model_constants에서 추가한 권한 정보를 담기위해서
    @ElementCollection(fetch = FetchType.EAGER) // 컬렉션 매핑을 위한 어노테이션
    private List<String> roles;//read write권한 둘다 가질수 있으므로 복수로 만듬

    //implements UserDetails에서의 자동으로 구현되는 메서드들
    //추후에 고도화된 기능 구현시 필요
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // true로 수정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // true로 수정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true로 수정
    }

    @Override
    public boolean isEnabled() {
        return true; // true로 수정
    }

}
