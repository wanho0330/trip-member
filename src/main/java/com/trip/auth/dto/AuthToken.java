package com.trip.auth.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
public class AuthToken {
    private String accessToken;
    private String refreshToken;
}
