package com.tss.service;

public class CashPaymentService implements IPaymentService{

    @Override
    public void doPayment(double amount) {
        System.out.println("Processing cash payment of Rs. " + amount);
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Cash will be collected on delivery.");
    }
}
