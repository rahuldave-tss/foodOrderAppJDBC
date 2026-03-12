package com.tss.entity;


public class Discount implements DiscountStrategy {
    private int id;
    private String name;
    private double discountAmount;
    private double discountPercentage;

    public Discount(int id, String name, double discountAmount, double discountPercentage) {
        this.id=id;
        this.name=name;
        this.discountAmount = discountAmount;
        this.discountPercentage = discountPercentage;
    }
    public Discount(String name, double discountAmount, double discountPercentage) {
        this.name=name;
        this.discountAmount = discountAmount;
        this.discountPercentage = discountPercentage;
    }

    public int getId() {
        return id;
    }

    @Override
    public double applyDiscount(double discountAmount) {
        return Math.max(0, discountAmount*this.discountPercentage/100);
    }

    public int getDiscountId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", amount=" + discountAmount +
                ", discountPercentage=" + discountPercentage +
                '}';
    }
}
