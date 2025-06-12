package dev.typhoon.chat_redis.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.typhoon.chat_redis.common.auth.JWTUtil;
import dev.typhoon.chat_redis.model.constant.Platform;
import dev.typhoon.chat_redis.model.constant.Role;
import dev.typhoon.chat_redis.model.dto.member.MemberInfoResponse;
import dev.typhoon.chat_redis.model.dto.member.MemberLoginRequest;
import dev.typhoon.chat_redis.model.entity.Member;
import dev.typhoon.chat_redis.model.vo.member.MemberToken;
import dev.typhoon.chat_redis.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public MemberToken login(MemberLoginRequest memberLoginRequest) {
        if (memberLoginRequest.getPlatform() != Platform.KAKAO) {
            throw new IllegalArgumentException("Invalid platform");
        }

        // todo: 카카오 유저 정보 조회
        String email = "test@test.com";

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member member;
        // 멤버 정보가 없는 경우 회원가입
        if (optionalMember.isEmpty()) {
            member = Member.builder()
                    .email(email)
                    .roles(Set.of(Role.ROLE_USER))
                    .deleted(false)
                    .build();
            memberRepository.save(member);
        } else {
            member = optionalMember.get();
        }

        MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                .email(member.getEmail())
                .roles(member.getRoles())
                .build();

        return MemberToken.builder()
                .accessToken("Bearer " + jwtUtil.generateAccessToken(member))
                .refreshTokenCookie(jwtUtil.generateRefreshCookie(member))
                .memberInfoResponse(memberInfoResponse)
                .build();
    }
}
