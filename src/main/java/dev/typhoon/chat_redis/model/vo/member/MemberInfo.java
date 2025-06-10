package dev.typhoon.chat_redis.model.vo.member;

import java.util.Set;

import dev.typhoon.chat_redis.model.constant.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfo {
    private String email;
    private Set<Role> roles;
}
