package com.trip.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long idx) {
        super(idx.toString() + " not found");
    }
    public UserNotFoundException() {super("userIdx not found");}
}
