package com.minjoo.StarlightWing.service.impl;

import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.persist.UserRepository;
import com.minjoo.StarlightWing.service.UserService;
import com.minjoo.StarlightWing.utils.TokenUtils;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String register(UserDto userDto) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getUserpw());
        userDto.setUserpw(encodedPassword);


        userRepository.save(userDto);

        // JWT 토큰 생성
        String token = TokenUtils.generateJwt(userDto);
        String refreshToken = TokenUtils.generateRefreshToken(userDto);
        return "AccessToken: " + token + ", RefreshToken: " + refreshToken;
    }

    @Override
    public Optional<UserDto> login(UserDto userDto) {
        Optional<UserDto> user = userRepository.findByUsernm(userDto.getUsernm());

        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(userDto.getUserpw(), user.get().getUserpw())) {
            return Optional.empty();
        }

        return user;  // 로그인 성공
    }

    @Override
    public List<UserDto> selectUserList(UserDto userDto) {
        return userRepository.findAll();  // 모든 사용자 목록 조회
    }
}
