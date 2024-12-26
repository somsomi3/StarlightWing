package com.minjoo.StarlightWing.service.impl;


import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.service.UserService;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) {

        if (userId == null || userId.isEmpty()) {
            throw new AuthenticationServiceException("사용자 ID가 비어있습니다.");
        }

        UserDto userDto = UserDto.builder().userid(Long.valueOf(userId)).build();
        Optional<UserDto> user = userService.login(userDto);


        if (user.isEmpty()) {
            throw new BadCredentialsException("사용자 정보가 올바르지 않습니다: " + userId);
        }

        return user.get();
    }
}