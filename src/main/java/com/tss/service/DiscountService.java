package com.tss.service;

import com.tss.entity.Discount;
import com.tss.entity.DiscountStrategy;
import com.tss.repository.impl.DiscountRepo;

import java.util.List;

public class DiscountService {
    private DiscountRepo discountRepo;

    public DiscountService(DiscountRepo discountRepo) {
        this.discountRepo = discountRepo;
    }

    public DiscountStrategy applyMaxDiscount(double cartTotal) {
        return discountRepo.getMaxPossibleDiscount(cartTotal);
    }
}
