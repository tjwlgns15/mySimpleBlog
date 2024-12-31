package com.jihun.mysimpleblog.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.auth.config.core.CustomUserDetailsService;
import com.jihun.mysimpleblog.auth.jwt.filter.JwtAuthenticationFilter;
import com.jihun.mysimpleblog.auth.jwt.filter.JwtAuthorizationFilter;
import com.jihun.mysimpleblog.auth.jwt.JwtTokenProvider;
import com.jihun.mysimpleblog.auth.oauth2.CustomOAuth2UserService;
import com.jihun.mysimpleblog.auth.oauth2.OAuth2UserInfoFactory;
import com.jihun.mysimpleblog.auth.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
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
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilter(corsConfig().corsFilter())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/", "/home/**").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/home/login")
                        .defaultSuccessUrl("/user")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService()))
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                )
                .build();
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
