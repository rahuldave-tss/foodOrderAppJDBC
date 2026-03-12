package com.tss.exceptions;

public class ItemAlreadyPresentException extends RuntimeException {
    public ItemAlreadyPresentException(String message) {
        super(message);
    }
}
