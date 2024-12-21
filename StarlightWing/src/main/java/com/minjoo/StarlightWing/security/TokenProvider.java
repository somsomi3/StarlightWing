package com.minjoo.StarlightWing.security;

//import antlr.StringUtils;
import com.minjoo.StarlightWing.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 60; //1시간
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secretKey;
    @PostConstruct
    public void printSecretKey() {
        System.out.println("Loaded JWT Secret Key: " + secretKey);
    }
    /**
     * 토큰 생성 메서드(발급)
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRED_TIME);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)   //토큰 생성시간
            .setExpiration(expiredDate) //토큰 만료시간
            .signWith(SignatureAlgorithm.HS512, secretKey) // 수정된 부분// 사용할 암호화 알고리즘과 비밀키
            .compact();

    }

    //토큰으로부터 인증정보를 가져오는 메서드
    public Authentication getAuthentication(String jwt){
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();

    }
    public boolean validateToken(String token){
        if (!StringUtils.hasText(token)) return false;
        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());

    }

    private Claims parseClaims(String token) {
        try{
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();

        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
