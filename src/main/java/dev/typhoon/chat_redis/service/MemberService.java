package dev.typhoon.chat_redis.service;

import org.springframework.stereotype.Service;

import dev.typhoon.chat_redis.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
}
