package com.tss.entity;

import com.tss.exceptions.EmptyCartException;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    private int id;
    private int customerId;

    public Cart(){}

    public Cart(int id, int customerId){
        this.id = id;
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }
}