package dev.typhoon.chat_redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.typhoon.chat_redis.model.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
