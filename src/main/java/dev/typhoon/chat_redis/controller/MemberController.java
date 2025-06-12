package dev.typhoon.chat_redis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.typhoon.chat_redis.common.auth.JWTUtil;
import dev.typhoon.chat_redis.model.dto.member.MemberInfoResponse;
import dev.typhoon.chat_redis.model.dto.member.MemberLoginRequest;
import dev.typhoon.chat_redis.model.vo.member.MemberToken;
import dev.typhoon.chat_redis.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

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

        MemberToken memberToken = memberService.login(memberLoginRequest);

        response.addHeader("Authorization", memberToken.getAccessToken());
        response.addCookie(memberToken.getRefreshTokenCookie());
        return ResponseEntity.ok().body(memberToken.getMemberInfoResponse());
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("test");
    }

}
