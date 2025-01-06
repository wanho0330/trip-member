package com.trip.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class GetUsersByName {
    @Getter
    public static class Req{
        private String name;
    }

    @Getter
    @Builder
    public static class Res {
        private List<User> users;
    }
}
