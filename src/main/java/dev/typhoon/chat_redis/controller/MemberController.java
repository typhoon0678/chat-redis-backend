package dev.typhoon.chat_redis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.typhoon.chat_redis.model.dto.member.MemberInfoResponse;
import dev.typhoon.chat_redis.model.dto.member.MemberLoginRequest;
import dev.typhoon.chat_redis.model.dto.member.MemberSignupRequest;
import dev.typhoon.chat_redis.model.vo.member.MemberToken;
import dev.typhoon.chat_redis.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<MemberInfoResponse> login(
            @RequestBody MemberLoginRequest memberLoginRequest,
            HttpServletResponse response) {

        MemberToken memberToken = memberService.login(memberLoginRequest);

        response.addHeader("Authorization", memberToken.getAccessToken());
        response.addCookie(memberToken.getRefreshTokenCookie());
        return ResponseEntity.ok().body(memberToken.getMemberInfoResponse());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberSignupRequest signupRequest) {
        memberService.signup(signupRequest);
        
        return ResponseEntity.ok().build();
    }
    

}
