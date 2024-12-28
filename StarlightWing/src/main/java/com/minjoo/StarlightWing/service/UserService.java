package com.minjoo.StarlightWing.service;

import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.persist.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@ComponentScan
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void register(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username은 필수");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password는 필수");
        }
        // 이메일 유효성 검사 추가
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일은 필수");
        }
        if (!userDto.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("올바르지 않은 이메일 형식입니다.");
        }
//        // DB에 저장할 User 엔티티 생성
//        User user = User.builder()
//            .username(userDto.getUsername())
//            .password(userDto.getPassword()) // 여기서 비밀번호는 반드시 암호화해야 합니다.
//            .email(userDto.getEmail())
//            .build();

        // DB에 저장
        userRepository.save(userDto);

    };

    public Optional<UserDto> login(UserDto userDto) {
        return userRepository.findByUsername(userDto.getUsername());
    }

    public List<UserDto> selectUserList(UserDto userDto) {
        return null;
    }
}
