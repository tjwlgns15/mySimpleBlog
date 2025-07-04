package com.jihun.mysimpleblog.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.infra.exception.ErrorCode;
import com.jihun.mysimpleblog.infra.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jihun.mysimpleblog.infra.exception.ErrorCode.*;


@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponseEntity;
        Map<String, String> data = new HashMap<>();

        if (exception instanceof BadCredentialsException) {
            apiResponseEntity = ApiResponse.error(LOGIN_FAILED, data);
        } else if (exception instanceof UsernameNotFoundException) {
            apiResponseEntity = ApiResponse.error(USER_NOT_FOUND, data);
        } else if (exception instanceof CredentialsExpiredException) {
            apiResponseEntity = ApiResponse.error(CREDENTIALS_EXPIRED, data);
        } else {
            apiResponseEntity = ApiResponse.error(AUTHENTICATION_FAILED, data);
        }

        mapper.writeValue(response.getWriter(), apiResponseEntity);
    }
}