package dev.typhoon.chat_redis.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.typhoon.chat_redis.common.auth.JWTUtil;
import dev.typhoon.chat_redis.common.exception.CustomException;
import dev.typhoon.chat_redis.common.exception.ErrorCode;
import dev.typhoon.chat_redis.model.constant.Platform;
import dev.typhoon.chat_redis.model.constant.Role;
import dev.typhoon.chat_redis.model.dto.member.MemberInfoResponse;
import dev.typhoon.chat_redis.model.dto.member.MemberLoginRequest;
import dev.typhoon.chat_redis.model.dto.member.MemberSignupRequest;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberToken login(MemberLoginRequest memberLoginRequest) {
        Member loginMember;

        switch (memberLoginRequest.getPlatform()) {
            case Platform.SERVER:
                loginMember = serverLogin(memberLoginRequest);
                break;
            case Platform.KAKAO:
                loginMember = kakaoLogin(memberLoginRequest);
                break;
            default:
                throw new CustomException(ErrorCode.DEFAULT_BAD_REQUEST);
        }

        return MemberToken.builder()
                .accessToken("Bearer " + jwtUtil.generateAccessToken(loginMember))
                .refreshTokenCookie(jwtUtil.generateRefreshCookie(loginMember))
                .memberInfoResponse(MemberInfoResponse.from(loginMember))
                .build();
    }

    // 서버 로그인 (ID, PW)
    private Member serverLogin(MemberLoginRequest memberLoginRequest) {
        String email = memberLoginRequest.getEmail();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.MEMBER_LOGIN_FAILED);
        }

        Member member = optionalMember.get();
        if (!passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.MEMBER_LOGIN_FAILED);
        }

        return member;
    }

    // 카카오 로그인 (kakao accessToken)
    private Member kakaoLogin(MemberLoginRequest memberLoginRequest) {
        String email = memberLoginRequest.getEmail();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member member;
        // 멤버 정보가 없는 경우 회원가입
        if (optionalMember.isEmpty()) {
            member = Member.builder()
                    .email(email)
                    .platform(Platform.KAKAO)
                    .roles(Set.of(Role.ROLE_USER))
                    .deleted(false)
                    .build();
            memberRepository.save(member);
        } else {
            member = optionalMember.get();
        }

        // todo: kakao 로그인 로직 추가
        throw new CustomException(ErrorCode.DEFAULT_BAD_REQUEST);
    }

    @Transactional
    public void signup(MemberSignupRequest signupRequest) {
        String email = signupRequest.getEmail();
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .platform(Platform.SERVER)
                .roles(Set.of(Role.ROLE_USER))
                .deleted(false)
                .build();

        memberRepository.save(member);
    }
}
