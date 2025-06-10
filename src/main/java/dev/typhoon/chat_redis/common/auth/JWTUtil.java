package dev.typhoon.chat_redis.common.auth;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import dev.typhoon.chat_redis.model.constant.Role;
import dev.typhoon.chat_redis.model.entity.Member;
import dev.typhoon.chat_redis.model.vo.member.MemberInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;

@Component
public class JWTUtil implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final int BEARER_PREFIX_LENGTH = 7;

    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.domain}")
    private String domain;

    @Value("${jwt.expire.access}")
    private int accessSeconds;

    @Value("${jwt.expire.refresh}")
    private int refreshSeconds;

    @Value("${jwt.expire.renew}")
    private int refreshRenewSeconds;

    @Value("${spring.profiles.default}")
    private String envMode;

    private SecretKey secretKey;

    @Override
    public void afterPropertiesSet() {
        this.secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    public String generateAccessToken(Member member) {
        return generateToken(member, ACCESS_TOKEN);
    }

    private String generateToken(Member member, String type) {
        String authorities = member.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.joining(","));

        return generateToken(member.getEmail(), authorities, type);
    }

    private String generateToken(String email, String authorities, String type) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + getExpirationTime(type));

        return Jwts.builder()
                .subject(email)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(secretKey)
                .expiration(validity)
                .compact();
    }

    private long getExpirationTime(String type) {
        return switch (type) {
            case ACCESS_TOKEN -> accessSeconds * 1000L;
            case REFRESH_TOKEN -> refreshSeconds * 1000L;
            default -> 0L;
        };
    }

    public Cookie generateRefreshCookie(Member member) {
        String refreshToken = generateToken(member, REFRESH_TOKEN);
        return generateCookie(REFRESH_TOKEN, refreshToken, refreshSeconds);
    }

    public Cookie generateRefreshCookie(String email, String authorities) {
        String refreshToken = generateToken(email, authorities, REFRESH_TOKEN);
        return generateCookie(REFRESH_TOKEN, refreshToken, refreshSeconds);
    }

    public Cookie generateLogoutCookie() {
        return generateCookie(REFRESH_TOKEN, "", 0);
    }

    private Cookie generateCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }

    // Token Validation Methods
    public boolean validateToken(String authorization) {
        if (!isValidAuthorizationHeader(authorization)) {
            return false;
        }

        String accessToken = getAccessTokenFromAuthorization(authorization);
        return validateJwtToken(accessToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return StringUtils.hasText(refreshToken) && validateJwtToken(refreshToken);
    }

    private boolean isValidAuthorizationHeader(String authorization) {
        return authorization != null && authorization.length() > BEARER_PREFIX_LENGTH;
    }

    private boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | ExpiredJwtException ignored) {
            return false;
        }
    }

    // refreshToken 갱신 여부 확인
    public boolean shouldRenewRefresh(String jwt) {
        Claims claims = getClaims(jwt);
        long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
        return remainingTime < refreshRenewSeconds * 1000L;
    }

    public Authentication getAuthentication(String jwt) {
        Claims claims = getClaims(jwt);
        Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }

    public MemberInfo getMemberInfo(String jwt) {
        Claims claims = getClaims(jwt);
        Set<Role> roles = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        return MemberInfo.builder()
                .email(claims.getSubject())
                .roles(roles)
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        return Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public String getAccessTokenFromAuthorization(String authorization) {
        return authorization.substring(BEARER_PREFIX_LENGTH);
    }
}
