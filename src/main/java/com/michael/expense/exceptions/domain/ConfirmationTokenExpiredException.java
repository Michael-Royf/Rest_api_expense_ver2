package com.michael.expense.exceptions.domain;

public class ConfirmationTokenExpiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ConfirmationTokenExpiredException(String message) {
        super(message);
    }
}
