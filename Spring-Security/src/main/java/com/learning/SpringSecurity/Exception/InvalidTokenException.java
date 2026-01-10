package com.learning.SpringSecurity.Exception;

public class InvalidTokenException extends RuntimeException {

    public  InvalidTokenException(String message){
        super(message);
    }
}
