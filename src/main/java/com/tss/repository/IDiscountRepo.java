package com.tss.repository;

import com.tss.entity.Discount;
import com.tss.entity.DiscountStrategy;

import java.util.List;

public interface IDiscountRepo {
    List<DiscountStrategy> getAvailableDiscounts();
    int addDiscount(DiscountStrategy discount);
    boolean removeDiscount(int discountId);
    DiscountStrategy findById(int id);
    boolean findDiscountByAmount(double amount);
    void updateDiscountPercentage(int id, double percentage);
    DiscountStrategy getMaxPossibleDiscount(double total);
}
