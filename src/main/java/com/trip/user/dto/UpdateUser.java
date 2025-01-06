package com.trip.user.dto;

import lombok.Builder;
import lombok.Getter;

public class UpdateUser {
    @Getter
    public static class Req {
        private User user;
    }

    @Getter
    @Builder
    public static class Res {
        private User user;
    }
}
