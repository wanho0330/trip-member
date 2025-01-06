package com.trip.auth.dto;

import lombok.Builder;
import lombok.Getter;

public class LogoutUser {

    @Getter
    public static class Req {
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class Res {

    }
}
