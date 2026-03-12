package com.tss.service;

import com.tss.entity.DiscountStrategy;
import com.tss.repository.impl.DiscountRepo;

import java.util.List;

public class DiscountService {
    private DiscountRepo discountRepo;

    public DiscountService(DiscountRepo discountRepo) {
        this.discountRepo = discountRepo;
    }

    public double applyMaxDiscount(double cartTotal){
        double maxDiscount=0;
        List<DiscountStrategy> availableDiscounts=discountRepo.getAvailableDiscounts();
        for(DiscountStrategy discount:availableDiscounts){
            if(cartTotal>=discount.getDiscountAmount()){
                maxDiscount=Math.max(maxDiscount,discount.applyDiscount(cartTotal));
            }
        }
        return maxDiscount;
    }
}
