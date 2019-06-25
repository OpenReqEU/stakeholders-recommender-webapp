package com.upc.gessi.spring.exception;

public class NotificationException extends Exception {

    private String message;

    public NotificationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
