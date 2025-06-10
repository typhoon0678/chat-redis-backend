package dev.typhoon.chat_redis.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Platform {
    KAKAO("KAKAO");

    private final String platform;
}
