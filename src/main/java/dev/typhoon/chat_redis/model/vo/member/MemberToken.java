package dev.typhoon.chat_redis.model.vo.member;

import dev.typhoon.chat_redis.model.dto.member.MemberInfoResponse;
import jakarta.servlet.http.Cookie;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberToken {
    private String accessToken;
    private Cookie refreshTokenCookie;
    private MemberInfoResponse memberInfoResponse;
}
