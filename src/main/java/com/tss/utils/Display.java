package com.tss.utils;

import com.tss.entity.FoodItem;

import java.util.List;

public class Display {
    public static void displayFoodItemHeader(){
        System.out.printf("%-10s %-20s %-10s%n", "ID", "Name", "Price");
        System.out.println("----------------------------------------------");
    }

    public static void displayMenu(List<FoodItem> items){
        displayFoodItemHeader();
        items.forEach(System.out::println);
    }
}
