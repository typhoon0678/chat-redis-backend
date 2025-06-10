package dev.typhoon.chat_redis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.typhoon.chat_redis.model.constant.Platform;
import dev.typhoon.chat_redis.model.constant.Role;
import dev.typhoon.chat_redis.model.dto.member.MemberInfoResponse;
import dev.typhoon.chat_redis.model.dto.member.MemberLoginRequest;
import dev.typhoon.chat_redis.service.MemberService;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<MemberInfoResponse> login(@RequestBody MemberLoginRequest memberLoginRequest) {
        if (memberLoginRequest.getPlatform() == Platform.KAKAO) {
            MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                    .email("test@test.com")
                    .roles(Set.of(Role.ROLE_USER))
                    .build();
            return ResponseEntity.ok().body(memberInfoResponse);
        }
        return ResponseEntity.badRequest().build();
    }

}
