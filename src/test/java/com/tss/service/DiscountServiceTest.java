package com.tss.service;

import com.tss.entity.Discount;
import com.tss.entity.DiscountStrategy;
import com.tss.repository.impl.DiscountRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    DiscountRepo discountRepo;

    @InjectMocks
    DiscountService discountService;

    @Test
    void shouldReturnDiscount(){
        Discount discount=new Discount(1,"Amount",500,10);

        when(discountRepo.getMaxPossibleDiscount(1000))
                .thenReturn(discount);

        DiscountStrategy result=discountService.applyMaxDiscount(1000);

        assertEquals(discount,result);
    }
}