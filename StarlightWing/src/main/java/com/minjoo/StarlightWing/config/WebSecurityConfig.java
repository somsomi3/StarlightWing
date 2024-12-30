package com.minjoo.StarlightWing.config;


import com.minjoo.StarlightWing.config.filter.CustomAuthenticationFilter;
import com.minjoo.StarlightWing.config.filter.JwtAuthorizationFilter;
import com.minjoo.StarlightWing.config.handler.CustomAuthFailureHandler;
import com.minjoo.StarlightWing.config.handler.CustomAuthSuccessHandler;
import com.minjoo.StarlightWing.config.handler.CustomAuthenticationProvider;
import com.minjoo.StarlightWing.config.handler.CustomLogoutHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("CORS 설정 초기화 완료2");

        return http
            .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("api/v1/user/index", "api/v1/user/index.html", "/static/**", "/login.js").permitAll()
                .requestMatchers("api/v1/user/login", "api/v1/user/register", "/api/v1/user/refresh","/api/v1/posts").permitAll()
                .requestMatchers("/api/v1/user/main").authenticated()  // 인증된 사용자만 접근 가능
                .anyRequest().authenticated())  // 나머지 요청은 인증 필요
            .addFilterBefore(jwtAuthorizationFilter(), BasicAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(this::configureLogout)  // 로그아웃
            .build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(passwordEncoder());
    }


    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("api/v1/user/login");     // 접근 URL
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }
    @Bean
    public AuthenticationFailureHandler customLoginFailureHandler() {
        return new CustomAuthFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return new CustomAuthSuccessHandler();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    //cors설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("CORS 설정이 호출되었습니다.");

        CorsConfiguration configuration = new CorsConfiguration();

        System.out.println("CORS 설정 적용");
        configuration.setAllowedOrigins(List.of("http://localhost:8080/index", "http://localhost:8000", "http://localhost:3000", "http://localhost:63342"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "x-refresh-token", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        System.out.println("CORS 설정 초기화 완료");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    private void configureLogout(@NonNull LogoutConfigurer<HttpSecurity> logout) {
        logout

                .logoutUrl("/api/v1/user/logout")
                //로그아웃 핸들러에서 추가 로직 마저구현(유효한지, 블랙리스트 등)
                .addLogoutHandler(customLogoutHandlerr())
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK));
    }

    @Bean
    public LogoutHandler customLogoutHandlerr() {
        return new CustomLogoutHandler();
    }
}
