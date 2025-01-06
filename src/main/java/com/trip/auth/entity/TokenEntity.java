package com.trip.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(value = "AuthToken",timeToLive = 151200)
public class TokenEntity {

    /**
     * 토큰문자열
     */
    @Id
    private String token;
    /**
     * user 인덱스
     */
    private String idx;

    @Builder
    public TokenEntity(String token, String idx) {
        this.token = token;
        this.idx = idx;
    }
}
