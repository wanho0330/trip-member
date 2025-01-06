package com.trip.user.dto;

import lombok.Builder;
import lombok.Getter;

public class GetUserByIdx {
    @Getter
    public static class Req {
        private Long idx;
    }

    @Getter
    @Builder
    public static class Res {
        private User user;
    }
}
