package com.minjoo.StarlightWing.web;


import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("api/v1/token")
public class TokenController {

    @PostMapping("/token")
    public ResponseEntity<Object> generateToken(@RequestBody UserDto userDto) {
        System.out.println("토큰을 생성");
        String resultToken = TokenUtils.generateJwt(userDto);
        return new ResponseEntity<>(resultToken, HttpStatus.OK);
    }
}
