package com.tss.repository;

import com.tss.entity.FoodItem;

import java.util.List;

public interface IMenuRepo {
    List<FoodItem> getMenu();
    void addItem(FoodItem foodItem);
    void removeItem(FoodItem foodItem);
    FoodItem getFoodItemById(int foodItemId);
}
