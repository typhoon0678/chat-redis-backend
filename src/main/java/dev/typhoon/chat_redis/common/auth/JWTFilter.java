package dev.typhoon.chat_redis.common.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends GenericFilterBean {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JWTUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorization = httpRequest.getHeader(AUTHORIZATION_HEADER);
        if (jwtUtil.validateToken(authorization)) {
            String accessToken = jwtUtil.getAccessTokenFromAuthorization(authorization);
            Authentication authentication = jwtUtil.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
