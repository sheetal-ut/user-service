package com.ps.user.exception;

public class DuplicateUserException extends RuntimeException{
    public DuplicateUserException(String message){
        super(message);
    }
    public DuplicateUserException(String message, Object... args) {
        super(String.format(message, args));
    }
}
