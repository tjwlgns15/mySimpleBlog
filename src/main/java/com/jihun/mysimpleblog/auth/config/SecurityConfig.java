package com.jihun.mysimpleblog.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.auth.config.core.CustomUserDetailsService;
import com.jihun.mysimpleblog.auth.config.handler.CustomAccessDeniedHandler;
import com.jihun.mysimpleblog.auth.config.handler.CustomAuthenticationEntryPoint;
import com.jihun.mysimpleblog.auth.config.handler.CustomLogoutHandler;
import com.jihun.mysimpleblog.auth.jwt.filter.JwtAuthenticationFilter;
import com.jihun.mysimpleblog.auth.jwt.filter.JwtAuthorizationFilter;
import com.jihun.mysimpleblog.auth.jwt.JwtTokenProvider;
import com.jihun.mysimpleblog.auth.oauth2.CustomOAuth2UserService;
import com.jihun.mysimpleblog.auth.oauth2.OAuth2UserInfoFactory;
import com.jihun.mysimpleblog.auth.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(corsConfig().corsFilter())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        configureAuthorization(http);
        configureExceptionHandling(http);
        configureOAuth2(http);
        configureLogout(http);

        return http.build();
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능한 엔드포인트
                .requestMatchers(
                        // 인증 관련
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/oauth2/**",
                        "/auth/**",

                        // 정적 리소스
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",

                        // 홈 관련 뷰
                        "/",
                        "/home/**",

                        // 게시판 조회
                        "/boards",
                        "/api/posts",
                        "/api/posts/{id}",
                        "/api/posts/{postId}/comments",
                        "/api/categories"
                ).permitAll()

                // 인증된 사용자만 접근 가능한 엔드포인트
                .requestMatchers(
                        // 게시글 작성/수정/삭제
                        "/api/posts/new",
                        "/api/posts/{id}",  // PUT, DELETE

                        // 댓글 작성/수정/삭제
                        "/api/posts/{postId}/comments/new",
                        "/api/posts/{postId}/comments/{commentId}",

                        // 좋아요
                        "/api/posts/{postId}/like",

                        // 사용자 정보
                        "/api/auth/me",
                        "/user/**",
                        "/my-page"
                ).hasAnyRole("USER", "ADMIN")

                // 관리자만 접근 가능한 엔드포인트
                .requestMatchers(
                        "/admin/**",
                        "/api/admin/**",
                        "/api/categories/new",
                        "/api/categories/{id}"  // PUT, DELETE
                ).hasRole("ADMIN")

                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
        );
    }

    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(handling -> handling
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
                .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))
        );
    }

    private void configureOAuth2(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/user")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService()))
                .successHandler(oAuth2AuthenticationSuccessHandler())
        );
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/")
                .addLogoutHandler(new CustomLogoutHandler())
                .clearAuthentication(true)
                .invalidateHttpSession(true)
        );
    }

    @Bean
    public CorsConfig corsConfig() {
        return new CorsConfig();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/api/auth/login");
        return filter;
    }
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtTokenProvider, userRepository);
    }
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    // OAuth2 관련 빈
    @Bean
    public OAuth2UserInfoFactory oAuth2UserInfoFactory() {
        return new OAuth2UserInfoFactory();
    }
    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(userRepository, oAuth2UserInfoFactory());
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtTokenProvider);
    }
}
