package com.tss.entity;

import com.tss.exceptions.EmptyCartException;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    //fooditem(id)->orderitem for no duplicates and easy access
    private Map<FoodItem, OrderItem> shoppingCart;

    public Cart() {
        shoppingCart = new HashMap<>();
    }


    public Map<FoodItem, OrderItem> getShoppingCart() {
        return shoppingCart;
    }

    public void addItem(OrderItem item) {
        FoodItem food = item.getFoodItem();
        if (shoppingCart.containsKey(food)) {
            OrderItem existingItem = shoppingCart.get(food);
            existingItem.setQuantity(
                    existingItem.getQuantity() + item.getQuantity()
            );
        } else {
            shoppingCart.put(food, item);
        }
    }

    public double getTotal() {
        double total = 0;

        for (OrderItem item : shoppingCart.values()) {
            total += item.getTotal();
        }

        return total;
    }

    public void clear(){
        shoppingCart.clear();
    }

    public void displayCart() {

        if (shoppingCart.isEmpty()) {
            throw new EmptyCartException();
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-10s %-10s %-12s%n",
                "ID", "NAME", "PRICE", "QTY", "SUBTOTAL");
        System.out.println("---------------------------------------------------------------------");

        for (OrderItem item : shoppingCart.values()) {

            int id = item.getFoodItem().getId();
            String name = item.getFoodItem().getName();
            double price = item.getFoodItem().getPrice();
            int quantity = item.getQuantity();
            double subtotal = price * quantity;

            System.out.printf("%-8d %-20s %-10.2f %-10d %-12.2f%n",
                    id, name, price, quantity, subtotal);
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-50s %-12.2f%n", "TOTAL AMOUNT:", getTotal());
        System.out.println("---------------------------------------------------------------------");
    }

    @Override
    public String toString() {
        return "Cart{" +
                "shoppingCart=" + shoppingCart +
                '}';
    }
}