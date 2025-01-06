package com.trip.auth.dto;

import lombok.Builder;
import lombok.Getter;

public class LoginUser {
    @Getter
    public static class Req {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class Res {
        private String accessToken;
        private String refreshToken;
    }
}
