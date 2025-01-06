package com.trip.common.exception;

import com.trip.common.dto.ErrorRes;
import com.trip.user.exception.EmailAlreadyExistsException;
import com.trip.user.exception.IncorrectPasswordException;
import com.trip.user.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorRes> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        ErrorRes res = ErrorRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorRes> handleIncorrectPasswordException(IncorrectPasswordException e) {
        ErrorRes res = ErrorRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorRes> handleUserNotFoundException(UserNotFoundException e) {
        ErrorRes res = ErrorRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();

        System.out.println("Generated res : " + res);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorRes> handleJwtException(JwtException e) {
        ErrorRes res = ErrorRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRes> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorRes res = ErrorRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRes> handleGenericException(Exception e) {
        ErrorRes res = ErrorRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }



}
