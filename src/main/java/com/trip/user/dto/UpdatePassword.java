package com.trip.user.dto;

import lombok.Builder;
import lombok.Getter;

public class UpdatePassword {
    @Getter
    public static class Req {
        private String oldPassword;
        private String newPassword;
    }

    @Getter
    @Builder
    public static class Res {
        private User user;
    }
}
