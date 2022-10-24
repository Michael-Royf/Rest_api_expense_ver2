package com.michael.expense.exceptions.domain;

public class PasswordExistException  extends  RuntimeException{
    private static final long serialVersionUID = 1L;
    public PasswordExistException(String message) {
        super(message);
    }
}
