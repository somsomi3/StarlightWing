package com.minjoo.StarlightWing.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.dto.ValidTokenDto;
import com.minjoo.StarlightWing.service.TokenBlackListService;
import com.minjoo.StarlightWing.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//토큰 검증
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenBlackListService tokenBlackListService;

    private static final String HTTP_METHOD_OPTIONS = "OPTIONS";
    private static final String ACCESS_TOKEN_HEADER_KEY = "Authorization";
    private static final String REFRESH_TOKEN_HEADER_KEY = "x-refresh-token";
    private static final List<String> WHITELIST_URLS = Arrays.asList(
        "/api/v1/user/index",
        "/api/v1/user/main",

        "/api/v1/user/login",
        "/api/v1/user/logout",

        "/api/v1/token/token",
        "/user/login",
        "/token/token",
        "/api/v1/user/register"  // 회원가입 요청 추가
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
        throws IOException, ServletException {

        System.out.println("JWT Authorization Filter가 호출되었습니다.");

        if (WHITELIST_URLS.contains(request.getRequestURI()) || HTTP_METHOD_OPTIONS.equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String accessTokenHeader = request.getHeader(ACCESS_TOKEN_HEADER_KEY);
            String refreshTokenHeader = request.getHeader(REFRESH_TOKEN_HEADER_KEY);

            if (StringUtils.isNotBlank(accessTokenHeader) || StringUtils.isNotBlank(refreshTokenHeader)) {
                System.out.println("토큰이 존재");

                String paramAccessToken = TokenUtils.getHeaderToToken(accessTokenHeader);
                String paramRefreshToken = TokenUtils.getHeaderToToken(refreshTokenHeader);

                // 블랙리스트에 있는 토큰일 경우, 거절
                if (tokenBlackListService.isContainToken(paramAccessToken)) {
                    throw new Exception("만료된 토큰입니다!");
                }

                ValidTokenDto accTokenValidDto = TokenUtils.isValidToken(paramAccessToken);
                if (accTokenValidDto.isValid()) {

                    String userId = TokenUtils.getClaimsToUserId(paramAccessToken);
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null,
                            Collections.singletonList(new SimpleGrantedAuthority("USER")));

                    SecurityContextHolder.getContext().setAuthentication(authentication);  // 인증 정보를 SecurityContext에 설정
                    log.info("User authenticated with ID: {}", userId);

                    Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                    if (currentAuth != null) {
                        log.info("Authenticated user: {}", currentAuth.getName());
                    } else {
                        log.warn("No authentication found in SecurityContext");
                    }
                    chain.doFilter(request, response);  // 접근을 허용
                }
                // 리프레시 토큰 생성
                else {
                    if (accTokenValidDto.getErrorName().equals("TOKEN_EXPIRED")) {
                        if (TokenUtils.isValidToken(paramRefreshToken).isValid()) {
                            UserDto claimsToUserDto = TokenUtils.getClaimsToUserDto(paramRefreshToken, false);
                            String token = TokenUtils.generateJwt(claimsToUserDto);         // 새로운 접근 토큰을 발급
                            sendToClientAccessToken(token, response);                       // 발급한 토큰을 클라이언트에게 전달
                            chain.doFilter(request, response);
                        } else {
                            throw new Exception("다시 로그인이 필요");
                        }
                    } else {
                        throw new Exception("토큰이 유효하지 않습니다.");
                    }
                }
            }

            else {
                throw new Exception("토큰이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            String jsonResponse = jwtTokenError(e);
            printWriter.print(jsonResponse);
            printWriter.flush();
            printWriter.close();
        }
    }


    private String jwtTokenError(Exception e) {
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> resultMap = new HashMap<>();
        String resultMsg = "";

        if (e instanceof ExpiredJwtException) {
            resultMsg = "토큰 기간이 만료";
        }

        else if (e instanceof JwtException) {
            resultMsg = "잘못된 토큰이 발급";
        }

        else {
            resultMsg = "OTHER TOKEN ERROR" + e;
        }
        // Custom Error Code 구성
        resultMap.put("status", 403);
        resultMap.put("code", "9999");
        resultMap.put("message", resultMsg);
        resultMap.put("reason", e.getMessage());

        try {
            return om.writeValueAsString(resultMap);
        } catch (JsonProcessingException err) {
            log.error("내부적으로 JSON Parsing Error 발생 " + err);
            return "{}";
        }
    }


    private void sendToClientAccessToken(String token, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        ObjectMapper om = new ObjectMapper();
        resultMap.put("status", 401);
        resultMap.put("failMsg", null);
        resultMap.put("accessToken", token);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(om.writeValueAsString(resultMap));
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            log.error("[-] 결과값 생성에 실패 : {}", e);
        }

    }
}
