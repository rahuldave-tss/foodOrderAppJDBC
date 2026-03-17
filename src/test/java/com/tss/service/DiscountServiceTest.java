package com.tss.service;

import com.tss.entity.Discount;
import com.tss.entity.DiscountStrategy;
import com.tss.repository.impl.DiscountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepo discountRepo;

    private DiscountService discountService;

    @BeforeEach
    void setUp() {
        discountService = new DiscountService(discountRepo);
    }

    @Test
    void testApplyMaxDiscountDelegatesToRepo() {
        Discount discount = new Discount(1, "Sale", 500.0, 10.0);
        when(discountRepo.getMaxPossibleDiscount(500.0)).thenReturn(discount);

        DiscountStrategy result = discountService.applyMaxDiscount(500.0);

        assertEquals(discount, result);
        verify(discountRepo, times(1)).getMaxPossibleDiscount(500.0);
    }

    @Test
    void testApplyMaxDiscountReturnsNull() {
        when(discountRepo.getMaxPossibleDiscount(50.0)).thenReturn(null);

        DiscountStrategy result = discountService.applyMaxDiscount(50.0);

        assertNull(result);
        verify(discountRepo, times(1)).getMaxPossibleDiscount(50.0);
    }
}