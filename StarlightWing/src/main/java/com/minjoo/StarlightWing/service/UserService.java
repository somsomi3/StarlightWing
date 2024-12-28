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
public abstract class UserService {

    @Autowired
    private UserRepository userRepository;

    public abstract String register(UserDto userDto);
    public Optional<UserDto> login(UserDto userDto) {
        return userRepository.findByUsername(userDto.getUsername());
    }

    public abstract List<UserDto> selectUserList(UserDto userDto);
}
