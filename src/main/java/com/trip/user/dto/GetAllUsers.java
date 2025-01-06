package com.trip.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class GetAllUsers {


    @Getter
    @Builder
    public static class Res {
        private List<User> users;
    }
}
