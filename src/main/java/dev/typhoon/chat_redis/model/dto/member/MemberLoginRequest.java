package dev.typhoon.chat_redis.model.dto.member;

import dev.typhoon.chat_redis.model.constant.Platform;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLoginRequest {

    private Platform platform;
    private String accessToken;
}
