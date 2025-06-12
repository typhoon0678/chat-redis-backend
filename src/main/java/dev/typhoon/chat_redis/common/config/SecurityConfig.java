package dev.typhoon.chat_redis.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import dev.typhoon.chat_redis.common.auth.CustomAccessDeniedHandler;
import dev.typhoon.chat_redis.common.auth.CustomAuthenticationEntryPoint;
import dev.typhoon.chat_redis.common.auth.JWTFilter;
import dev.typhoon.chat_redis.common.auth.JWTUtil;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JWTUtil jwtUtil;
        private final CorsFilter corsFilter;
        private final CustomAccessDeniedHandler customAccessDeniedHandler;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                String[] memberApi = { "/api/member/login" };
                String[] h2Console = { "/h2-console/**" };
                http
                                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                                .requestMatchers(memberApi).permitAll()
                                                .requestMatchers(h2Console).permitAll()
                                                .anyRequest().authenticated())

                                .csrf(AbstractHttpConfigurer::disable)
                                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)

                                .sessionManagement(
                                                sessionManagement -> sessionManagement
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .accessDeniedHandler(customAccessDeniedHandler)
                                                .authenticationEntryPoint(customAuthenticationEntryPoint));

                return http.build();
        }
}
