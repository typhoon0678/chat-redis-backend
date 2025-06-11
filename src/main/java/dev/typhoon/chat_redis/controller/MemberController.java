package dev.typhoon.chat_redis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.typhoon.chat_redis.common.auth.JWTUtil;
import dev.typhoon.chat_redis.model.constant.Platform;
import dev.typhoon.chat_redis.model.constant.Role;
import dev.typhoon.chat_redis.model.dto.member.MemberInfoResponse;
import dev.typhoon.chat_redis.model.dto.member.MemberLoginRequest;
import dev.typhoon.chat_redis.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<MemberInfoResponse> login(
            @RequestBody MemberLoginRequest memberLoginRequest,
            HttpServletResponse response) {
        if (memberLoginRequest.getPlatform() == Platform.KAKAO) {
            MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                    .email("test@test.com")
                    .roles(Set.of(Role.ROLE_USER))
                    .build();

            response.addHeader("Authorization", "Bearer " + "test");
            response.addCookie(jwtUtil.generateRefreshCookie("test@test.com", "ROLE_USER"));
            return ResponseEntity.ok().body(memberInfoResponse);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("test");
    }

}
