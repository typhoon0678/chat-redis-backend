package dev.typhoon.chat_redis.model.dto.member;

import java.util.Set;

import dev.typhoon.chat_redis.model.constant.Platform;
import dev.typhoon.chat_redis.model.constant.Role;
import dev.typhoon.chat_redis.model.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoResponse {

    private String email;
    private Platform platform;
    private Set<Role> roles;

    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .email(member.getEmail())
                .platform(member.getPlatform())
                .roles(member.getRoles())
                .build();
    }
}
