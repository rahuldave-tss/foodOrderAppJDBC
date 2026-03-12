package com.tss.repository;

import com.tss.entity.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class MenuRepo {
    List<FoodItem> menu;

    public MenuRepo() {
        this.menu=new ArrayList<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menu.add(new FoodItem("Burger", 50));
        menu.add(new FoodItem("Pizza", 200));
        menu.add(new FoodItem("Pasta", 100));
    }

    public List<FoodItem> getMenu() {
        return menu;
    }

    public void addItem(FoodItem foodItem){
        menu.add(foodItem);
    }
    public void removeItem(FoodItem foodItem){
        menu.remove(foodItem);
    }

    public FoodItem getFoodItemById(int itemId){
        for(FoodItem f:menu){
            if(f.getId()==itemId)return f;
        }
        return null;
    }
}
