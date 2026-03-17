package com.tss.service;

import com.tss.entity.Cart;
import com.tss.entity.CartItem;
import com.tss.entity.Customer;
import com.tss.entity.FoodItem;
import com.tss.repository.impl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock DPRepo dpRepo;
    @Mock DiscountRepo discountRepo;
    @Mock DiscountService discountService;
    @Mock DeliveryService deliveryService;
    @Mock OrderRepo orderRepo;
    @Mock OrderItemRepo orderItemRepo;
    @Mock PaymentRepo paymentRepo;
    @Mock CartRepo cartRepo;
    @Mock CartItemRepo cartItemRepo;
    @Mock Customer customer;
    @InjectMocks
    CustomerService customerService;

    @Test
    void shouldNotPlaceOrderWhenCartIsNull() throws SQLException {
        when(customer.getId()).thenReturn(1);
        when(cartRepo.getCartByUserId(1)).thenReturn(null);

        customerService.placeOrder();

        verify(orderRepo,never()).createOrder(anyInt(),any(),anyDouble(),any());
    }

    @Test
    void shouldNotPlaceOrderWhenCartItemsEmpty() throws SQLException {
        when(customer.getId()).thenReturn(1);
        Cart cart=new Cart();
        cart.setId(1);
         when(cartRepo.getCartByUserId(1)).thenReturn(cart);

         when(cartItemRepo.getCartItems(cart.getId())).thenReturn(List.of());
        customerService.placeOrder();

        verify(orderRepo,never()).createOrder(anyInt(),any(),anyDouble(),any());

    }
}