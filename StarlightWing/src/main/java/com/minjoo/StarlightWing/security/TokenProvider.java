package com.minjoo.StarlightWing.security;

//import antlr.StringUtils;
import com.minjoo.StarlightWing.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 60; //1시간
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("{spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성 메서드(발급)
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);//사용자권한정보를 저장하는 클레임
        claims.put(KEY_ROLES, roles);

        var now = new Date();//토큰생성시간
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRED_TIME);//토큰 만료시간

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)   //토큰 생성시간
            .setExpiration(expiredDate) //토큰 만료시간
            .signWith(SignatureAlgorithm.HS512, this.secretKey)// 사용할 암호화 알고리즘과 비밀키
            .compact();//compact라는 메서드를 써서 build를 끝내자

    }

    //토큰으로부터 인증정보를 가져오는 메서드
    public Authentication getAuthentication(String jwt){
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //2.넣어준 정보들이 나옴
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();

    }

    //3.토큰이 유효한지 아닌지 확인
    public boolean validateToken(String token){
        if (!StringUtils.hasText(token)) return false;
        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());

    }


    //토큰이 유효한지 알기 위해서, 1.토큰으로부터 클레임 정보를 가져오는 메서드
    private Claims parseClaims(String token) {
        try{
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();

        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
