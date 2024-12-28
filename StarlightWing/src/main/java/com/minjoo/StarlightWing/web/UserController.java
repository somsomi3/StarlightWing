package com.minjoo.StarlightWing.web;

import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.service.UserService;
import com.minjoo.StarlightWing.utils.TokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<Object> selectUser(@RequestBody UserDto userDto) {
        List<UserDto> selectUserList = userService.selectUserList(userDto);
        return new ResponseEntity<>(selectUserList, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserDto userDto) {
        userService.register(userDto);
        // 토큰 생성
        String token = TokenUtils.generateJwt(userDto);
        String refreshToken = TokenUtils.generateRefreshToken(userDto);

        return ResponseEntity.ok().body(Map.of(
            "message", "회원가입이 성공적으로 처리되었습니다.",
            "token", token,
            "refreshToken",refreshToken
        ));
    }

    @CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDto userDto, HttpServletResponse response) {
        // 로그 확인
        System.out.println("Received username: " + userDto.getUsername());
        System.out.println("Received password: " + userDto.getPassword());
        Optional<UserDto> isAuthenticated = userService.login(userDto);

        if (isAuthenticated.isPresent()) {
            // 토큰 생성
            String token = TokenUtils.generateJwt(isAuthenticated.get());
            String refreshToken = TokenUtils.generateRefreshToken(isAuthenticated.get());

            // Refresh Token을 httpOnly 쿠키에 저장
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true); // httpOnly 설정
            refreshTokenCookie.setSecure(false); // HTTPS 환경에서는 true로 설정
            refreshTokenCookie.setPath("/"); // 쿠키를 모든 경로에서 사용할 수 있도록 설정
            refreshTokenCookie.setMaxAge(14 * 24 * 60 * 60); // 14일

            response.addCookie(refreshTokenCookie);

            // Access Token을 JSON 응답으로 전송
            return ResponseEntity.ok().body(Map.of(
                "message", "로그인에 성공하였습니다.",
                "token", token
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", "인증에 실패하였습니다."
            ));
        }
    }


    @GetMapping("/index")
    public String index() {
        return "index";  // templates/index.html을 렌더링
    }

//
//    @ResponseBody
//    @CrossOrigin(origins = "http://localhost:8080")
//    @GetMapping("/main")
//    public ResponseEntity<?> getMainPage() {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "메인 페이지 데이터");
//        return ResponseEntity.ok(response); // JSON 형식으로 응답 반환
//    }

    @GetMapping("/main")
    public ResponseEntity<Map<String, Object>> getMainPageData(
        @RequestHeader(value = "Authorization") String authorizationToken,
        @RequestHeader(value = "x-refresh-token") String refreshToken) {

        try {
            // 토큰을 그대로 사용 (Bearer 접두어가 없으므로 바로 사용)
            String accessToken = authorizationToken;  // 그대로 사용

            // 토큰에서 사용자 정보 가져오기
            UserDto userDto = TokenUtils.getClaimsToUserDto(accessToken, true);  // true는 AccessToken 여부

            // 사용자 인증이 성공적이라면 메인 페이지 데이터 반환
            Map<String, Object> response = new HashMap<>();
            response.put("token", accessToken);  // 토큰을 응답에 포함시킴

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 Unauthorized 상태 코드와 에러 메시지 반환
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "인증에 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }



    @GetMapping("/")
    public String home() {
        return "index";  // "/" 경로로 접근해도 index.html로 이동
    }
}
