package com.minjoo.StarlightWing.config.handler;

import com.minjoo.StarlightWing.dto.UserDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

//    @Autowired
    private UserDetailsService userDetailsService;

    @NonNull
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("CustomAuthenticationProvider");

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;


        String userId = token.getName();
        String userPw = (String) token.getCredentials();


        UserDto userDto = (UserDto) userDetailsService.loadUserByUsername(userId);

        // 비밀번호 일치여부 판단
        if (!passwordEncoder.matches(userPw, userDto.getUserpw())) {
            throw new BadCredentialsException(userDto.getUsernm() + " Invalid password");
        }

        // 인증 성공
        return new UsernamePasswordAuthenticationToken(userDto, userPw, userDto.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
