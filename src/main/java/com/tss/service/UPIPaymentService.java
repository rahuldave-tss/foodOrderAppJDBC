package com.tss.service;

public class UPIPaymentService implements IPaymentService{

    @Override
    public void doPayment(double amount) {
        System.out.println("Processing UPI payment of Rs. " + amount);
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Payment successful via UPI.");
    }
}
