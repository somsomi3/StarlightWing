package com.minjoo.StarlightWing.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minjoo.StarlightWing.dto.ValidTokenDto;
import com.minjoo.StarlightWing.service.TokenBlackListService;
import com.minjoo.StarlightWing.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
@Slf4j
@Service
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private TokenBlackListService tokenBlackListService;

    private void sendErrorResponse(HttpServletResponse response, Map<String, Object> resultMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(resultMap);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        log.debug("[+] 로그아웃이 수행이 됩니다.");


        String headerToken = request.getHeader("Authorization");

        if (headerToken != null && !headerToken.isEmpty()) {

            String token = TokenUtils.getHeaderToToken(headerToken);
            log.debug("[+] 추출된 토큰: " + token);

            ValidTokenDto validTokenDto = TokenUtils.isValidToken(token);
            if (validTokenDto.isValid()) {

                if (!tokenBlackListService.isContainToken(token)) {
                    tokenBlackListService.addTokenToList(token);
                    List<Object> blackList = tokenBlackListService.getTokenBlackList();
                    log.debug("[+] 블랙리스트: " + blackList);
                    log.debug("[+] 블랙리스트 저장 완료, 로그아웃 완료.");
                    response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                } else {
                    log.warn("[+] 이미 블랙리스트에 있는 토큰입니다.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("resultCode", 9999);
                    resultMap.put("failMsg", "이미 로그아웃된 토큰입니다.");
                    sendErrorResponse(response, resultMap);
                }
            } else {
                // 잘못된 토큰인 경우
                log.error("[+] 유효하지 않은 토큰입니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("resultCode", 9999);
                resultMap.put("failMsg", "잘못된 토큰입니다.");
                sendErrorResponse(response, resultMap);
            }
        }

        else {
            log.error("[+] 토큰이 존재하지 않습니다.");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("resultCode", 9999);
            resultMap.put("failMsg", "로그아웃 과정에서 문제가 발생.");
            sendErrorResponse(response, resultMap);
        }
    }
}
