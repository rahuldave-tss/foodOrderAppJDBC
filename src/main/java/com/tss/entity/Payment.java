package com.tss.entity;

import com.tss.enums.PaymentType;

import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int orderId;
    private PaymentType paymentType;
    private double amount;
    private LocalDateTime paidAt;

    public Payment(int orderId, PaymentType paymentType, double amount) {
        this.orderId = orderId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paidAt = LocalDateTime.now();
    }

    public Payment(int id, int orderId, PaymentType paymentType, double amount, LocalDateTime paidAt) {
        this.id = id;
        this.orderId = orderId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
