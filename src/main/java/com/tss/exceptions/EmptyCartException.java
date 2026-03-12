package com.tss.exceptions;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("Cart is Empty !!");
    }
}
