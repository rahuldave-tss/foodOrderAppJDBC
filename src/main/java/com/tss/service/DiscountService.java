package com.tss.service;

import com.tss.entity.DiscountStrategy;
import com.tss.repository.impl.DiscountRepo;

import java.util.List;

public class DiscountService {
    private DiscountRepo discountRepo;

    public DiscountService(DiscountRepo discountRepo) {
        this.discountRepo = discountRepo;
    }

    public String applyMaxDiscount(double cartTotal) {
        double maxDiscount = 0;
        int discountId=-1;
        List<DiscountStrategy> availableDiscounts = discountRepo.getAvailableDiscounts();
        for (DiscountStrategy discount : availableDiscounts) {
            if (cartTotal >= discount.getDiscountAmount()) {
                double discountValue = discount.applyDiscount(cartTotal);
                if(discountValue > maxDiscount){
                    discountId=discount.getId();
                    maxDiscount = discountValue;
                }
            }
        }
        return maxDiscount+"-"+discountId;
    }
}
