package com.tss.repository.impl;

import com.tss.entity.Discount;
import com.tss.entity.DiscountStrategy;

import java.util.ArrayList;
import java.util.List;

public class DiscountRepo {
    private List<DiscountStrategy> availableDiscounts;

    public DiscountRepo(){
        availableDiscounts=new ArrayList<>();
        //initial discount
        this.addDiscount(new Discount("Amount Discount",500.0,10));
    }

    public List<DiscountStrategy> getAvailableDiscounts() {
        return availableDiscounts;
    }

    public void setAvailableDiscounts(List<DiscountStrategy> availableDiscounts) {
        this.availableDiscounts = availableDiscounts;
    }

    public boolean findDiscountByAmount(double amount){
        for(DiscountStrategy discount:availableDiscounts){
            if(discount instanceof Discount){
                Discount amountDiscount=(Discount) discount;
                if(amountDiscount.getDiscountAmount()==amount){
                    return true;
                }
            }
        }
        return false;
    }

    public void addDiscount(DiscountStrategy discount){
        availableDiscounts.add(discount);
    }

    public void removeDiscount(DiscountStrategy discount){
        availableDiscounts.remove(discount);
    }

    public DiscountStrategy findById(int id){
        return availableDiscounts.stream()
                .filter(d->d.getDiscountId()==id)
                .findFirst()
                .orElse(null);
    }

}
