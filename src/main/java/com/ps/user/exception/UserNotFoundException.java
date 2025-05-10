package com.ps.user.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
    public UserNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
