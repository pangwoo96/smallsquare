package com.smallsquare.config.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 기본 보안 설정 비활성화
        configureBasicSecurity(http);

        // JWT 필터 설정
        // configureJwtFilter(http);

        // 예외 처리 설정
        configureExceptionHandling(http);

        // 인증/인가 설정
        configureAuthorization(http);

        // 세션 설정
        configureSession(http);

        // 로그아웃 설정
        // configureLogout(http);

        // CORS 설정
        configureCors(http);

        http.securityContext(securityContext -> securityContext
                .securityContextRepository(new HttpSessionSecurityContextRepository())
        );

        return http.build();
    }


    @PostConstruct
    public void enableAuthContextPropagation() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // CORS 설정
    private void configureCors(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://dailyemotion.site"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 메서드
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 기본 보안 설정 비활성화
    private void configureBasicSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
    }

//    // JWT 필터 설정 추가
//    private void configureJwtFilter(HttpSecurity http) {
//        http.addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);
//    }

    // 인증/인가 예외 처리
    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(handling -> handling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\": \"인증이 필요합니다.\", \"path\": \""
                            + request.getRequestURI() + "\"}");
                })
        );
    }

    // URL별 인증/인가 규칙
    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllRequestMatchers()).permitAll()
                .requestMatchers(authenticatedRequestMatchers()).authenticated()
                .anyRequest().permitAll()
        );
    }

    // 인증 없이 접근 가능한 URL 패턴
    private RequestMatcher[] permitAllRequestMatchers() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher(POST, "/api/users/"),
                antMatcher("/swagger-ui/**"),
                antMatcher("/swagger-ui.html"),
                antMatcher("/v3/api-docs/**"),
                antMatcher("/api-docs/**")
        );
        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    // 인증이 필요한 URL 패턴
    private RequestMatcher[] authenticatedRequestMatchers() {
        List<RequestMatcher> requestMatchers = List.of(
                // 다이어리 CRUD
                antMatcher(GET, "/diaries/{date}")

        );
        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    // 세션 관리 설정 (JWT 사용 → STATELESS)
    private void configureSession(HttpSecurity http) throws Exception {
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/login"));
    }

//    // 로그아웃 설정
//    private void configureLogout(HttpSecurity http) throws Exception {
//        http.logout(logout -> logout
//                .logoutUrl("/logout")
//                .addLogoutHandler(customLogoutHandler)
//                .logoutSuccessHandler((request, response, authentication) ->
//                        response.setStatus(HttpServletResponse.SC_OK))
//        );
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
