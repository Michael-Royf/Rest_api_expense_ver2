package com.michael.expense.exceptions.domain;

public class TokenRefreshException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public TokenRefreshException(String message) {
        super(message);
    }
}
