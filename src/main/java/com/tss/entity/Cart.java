package com.tss.entity;

import com.tss.exceptions.EmptyCartException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private int id;
    private int customerId;

    public Cart(){}

    public Cart(int id, int customerId) {
        this.id = id;
        this.customerId=customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}