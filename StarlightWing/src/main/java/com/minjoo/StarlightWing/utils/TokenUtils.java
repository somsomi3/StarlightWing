package com.minjoo.StarlightWing.utils;


import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.dto.ValidTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class TokenUtils {

    private static SecretKey JWT_SECRET_KEY;

    public TokenUtils(@Value("${spring.jwt.secret}") String jwtSecretKey) {
        TokenUtils.JWT_SECRET_KEY = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }


    private static Date createExpiredDate(boolean isAccessToken) {
        Calendar c = Calendar.getInstance();

        if (isAccessToken) {
            c.add(Calendar.MINUTE, 15);        // 10초
            // c.add(Calendar.HOUR, 1);             // 1시간
            // c.add(Calendar.HOUR, 8);             // 8시간
            // c.add(Calendar.DATE, 1);             // 1일
        } else {
            //        c.add(Calendar.SECOND, 10);        // 10초
            c.add(Calendar.DATE, 14);
        }
        return c.getTime();
    }

    private static Map<String, Object> createHeader() {
        return Jwts.header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .add("regDate", System.currentTimeMillis()).build();
    }


    private static Map<String, Object> createClaims(UserDto userDto, boolean isAccessToken) {
        // 공개 클레임에 사용자의 이름과 이메일을 설정하여 정보를 조회할 수 있다.
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDto.getUserid());
        if (isAccessToken) {
            claims.put("userNm", userDto.getUsername());
        }
        return claims;
    }


    public static ValidTokenDto isValidToken(String token) {
        try {
            Claims claims = getTokenToClaims(token);
            log.info("expireTime :{}", claims.getExpiration());
            log.info("userId :" + claims.get("userId"));
            log.info("userNm :" + claims.get("userNm"));
            return ValidTokenDto.builder().isValid(true).errorName(null).build();
        } catch (ExpiredJwtException exception) {
            log.error("Token Expired", exception);
            return ValidTokenDto.builder().isValid(false).errorName("TOKEN_EXPIRED").build();
        } catch (JwtException exception) {
            log.error("Token Tampered", exception);
            return ValidTokenDto.builder().isValid(false).errorName("TOKEN_INVALID").build();
        } catch (NullPointerException exception) {
            log.error("Token is null", exception);
            return ValidTokenDto.builder().isValid(false).errorName("TOKEN_NULL").build();
        }
    }


    public static String generateJwt(UserDto userDto) {
        // 사용자 시퀀스를 기준으로 JWT 토큰을 발급하여 반환해줍니다.
        JwtBuilder builder = Jwts.builder()
                .setHeader(createHeader())                                  // Header 구성
                .claims(createClaims(userDto, true))        // Payload - Claims 구성
                .subject(String.valueOf(userDto.getUsername()))           // Payload - Subject 구성
                .signWith(JWT_SECRET_KEY)                               // Signature 구성
                .expiration(createExpiredDate(true));                       // Expired Date 구성
        return builder.compact();
    }


    public static String generateRefreshToken(UserDto userDto) {
        return Jwts.builder()
                .setHeader(createHeader())
                .claims(createClaims(userDto, false))
                .subject(String.valueOf(userDto.getUsername()))
                .signWith(JWT_SECRET_KEY)
                .expiration(createExpiredDate(false))
                .compact();
    }


//    public static String getHeaderToToken(String header) {
//        // 헤더가 null이거나 비어있을 경우 처리
//        if (header == null || header.trim().isEmpty()) {
//            throw new IllegalArgumentException("Header is missing or malformed");
//        }
//
//        // Bearer나 다른 접두어 없이 그냥 토큰만 처리
//        return header.trim();  // 헤더에서 공백을 제거하고 토큰 반환
//    }
    public static String getHeaderToToken(String header) {
        return header.split(" ")[1];
    }


    public static Claims getTokenToClaims(String token) {
        System.out.println("확인1  : " + token);
        System.out.println("확인2  : " + JWT_SECRET_KEY);
        return Jwts.parser()
                .verifyWith(JWT_SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public static String getClaimsToUserId(String token) {
        Claims claims = getTokenToClaims(token);
        return claims.get("userId").toString();
    }


    public static UserDto getClaimsToUserDto(String token, boolean isAccessToken) {
        Claims claims = getTokenToClaims(token);
        String userId = claims.get("userId").toString();
        if (isAccessToken) {
            String userNm = claims.get("userNm").toString();
            return UserDto.builder().userid(Long.valueOf(userId)).username(userNm).build();
        } else {
            return UserDto.builder().userid(Long.valueOf(userId)).build();
        }
    }
}

