package com.neerajbisht.projects.AirBnB.exception;

public class UnAuthorizeException extends RuntimeException{
    public UnAuthorizeException(String message) {
        super(message);
    }
}
