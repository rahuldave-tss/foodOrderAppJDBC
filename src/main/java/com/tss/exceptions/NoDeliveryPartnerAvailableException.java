package com.tss.exceptions;

public class NoDeliveryPartnerAvailableException extends RuntimeException {
    public NoDeliveryPartnerAvailableException() {
        super("No Delivery Partners Available !!");
    }
}
