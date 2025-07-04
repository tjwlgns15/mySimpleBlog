package com.jihun.mysimpleblog.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.mysimpleblog.infra.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

import static com.jihun.mysimpleblog.infra.exception.ErrorCode.UNAUTHORIZED_ACCESS;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper mapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
		// 상세 로그 추가
		log.error("접근 거부 발생: URL={}, Method={}, 원인={}",
				request.getRequestURI(),
				request.getMethod(),
				accessDeniedException.getMessage());

		// 요청 헤더 로깅
		log.debug("요청 헤더:");
		Collections.list(request.getHeaderNames()).forEach(headerName ->
				log.debug("  {} : {}", headerName, request.getHeader(headerName)));

		// 인증 정보 로깅
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			log.debug("인증 정보: Principal={}, Authorities={}",
					auth.getPrincipal(),
					auth.getAuthorities());
		} else {
			log.debug("인증 정보 없음");
		}

		// 예외 스택 트레이스 로깅
		log.debug("예외 상세:", accessDeniedException);

		// AJAX 요청인 경우 JSON 응답 반환
		if (isAjaxRequest(request)) {
			// HTTP 상태 코드는 200으로 설정
			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");

			ApiResponse<?> apiResponse = ApiResponse.error(UNAUTHORIZED_ACCESS);

			// 응답 쓰기
			mapper.writeValue(response.getWriter(), apiResponse);
		} else {
			// 일반 요청인 경우 접근 거부 페이지로 리다이렉트
			response.sendRedirect("/error/access-denied");
		}
	}

	/**
	 * AJAX 요청인지 확인
	 */
	private boolean isAjaxRequest(HttpServletRequest request) {
		String acceptHeader = request.getHeader("Accept");
		String xRequestedWith = request.getHeader("X-Requested-With");

		// Accept 헤더가 application/json인 경우 또는 X-Requested-With 헤더가 XMLHttpRequest인 경우
		return (acceptHeader != null && acceptHeader.contains("application/json")) ||
				"XMLHttpRequest".equals(xRequestedWith) ||
				request.getRequestURI().startsWith("/api/");
	}
}