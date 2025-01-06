package com.trip.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public class ErrorRes {
    private final String message;
    private final HttpStatus status;
    private final String timestamp;

    @Builder
    public ErrorRes(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now().toString();
    }
}
