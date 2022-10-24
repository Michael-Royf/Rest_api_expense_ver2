package com.michael.expense.exceptions.domain;

public class EmailAlreadyConfirmedException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }
}
