package com.trip.user.exception;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("password incorrect");
    }
}
