package com.tss.entity;


public class FoodItem {
    private int id;
    private String name;
    private double price;

    public FoodItem(int id,String name, double price) {
        this.id=id;
        this.name = name;
        this.price = price;
    }
    public FoodItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return String.format("%-10d %-20s %-10.2f",
                id, name, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodItem)) return false;

        FoodItem that = (FoodItem) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
