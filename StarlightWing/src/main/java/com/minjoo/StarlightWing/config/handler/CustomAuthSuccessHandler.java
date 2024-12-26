package com.minjoo.StarlightWing.config.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;


@Slf4j
@Configuration
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.debug("CustomLoginSuccessHandler");


        UserDto userDto = (UserDto) authentication.getPrincipal();


        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userInfo", userDto);


        if ("D".equals(userDto.getUsernm())) {
            responseMap.put("resultCode", 9001);
            responseMap.put("token", null);
            responseMap.put("failMsg", "휴면 계정입니다.");
        } else {
            responseMap.put("resultCode", 200);
            responseMap.put("failMsg", null);
            String accessToken = TokenUtils.generateJwt(userDto);
            String refreshToken = TokenUtils.generateRefreshToken(userDto);
            log.debug("생성된 토큰(접근 토큰) :: {}", accessToken);
            log.debug("생성된 토큰(리프레시 토큰) :: {}", accessToken);
            responseMap.put("accessToken", accessToken);
            responseMap.put("refreshToken", refreshToken);
            response.addHeader("Authorization", accessToken);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        printWriter.write(objectMapper.writeValueAsString(responseMap));
        printWriter.flush();
        printWriter.close();
    }
}