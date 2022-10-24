package com.michael.expense.exceptions.domain;

public class FailSendEmailException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public FailSendEmailException(String message) {
        super(message);
    }
}
