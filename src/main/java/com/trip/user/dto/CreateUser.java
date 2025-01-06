package com.trip.user.dto;

import lombok.Builder;
import lombok.Getter;

public class CreateUser {
    @Getter
    public static class Req {
        private String email;
        private String name;
        private String password;
    }

    @Getter
    @Builder
    public static class Res {
        private User user;
    }

    public static User ReqToUser(Req req) {
        return User.builder()
                .email(req.getEmail())
                .name(req.getName())
//                .password(req.getPassword())
                .build();
    }
}
