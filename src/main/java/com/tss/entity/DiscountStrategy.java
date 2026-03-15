package com.tss.entity;

public interface DiscountStrategy {
    double applyDiscount(double amount);
    int getDiscountId();
    void setDiscountPercentage(double discountPercentage);
    double getDiscountPercentage();
    double getDiscountAmount();
    int getId();
}
