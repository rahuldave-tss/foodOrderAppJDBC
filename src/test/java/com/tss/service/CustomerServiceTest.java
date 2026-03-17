package com.tss.service;

import com.tss.entity.Cart;
import com.tss.entity.CartItem;
import com.tss.entity.Customer;
import com.tss.entity.FoodItem;
import com.tss.repository.impl.*;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void init(){
        customer=new Customer(100,"r123","Rahul","123","rahul@gmail.com","9426545652");
        customerService=new CustomerService(customer,discountService,deliveryService,orderRepo,orderItemRepo,paymentRepo,cartRepo,cartItemRepo);
    }

    @Test
    void shouldNotPlaceOrderWhenCartIsNull() throws SQLException {
        when(cartRepo.getCartByUserId(1)).thenReturn(null);

        customerService.placeOrder();

        verify(orderRepo,never()).createOrder(anyInt(),any(),anyDouble(),any());
    }

    @Test
    void shouldNotPlaceOrderWhenCartItemsEmpty() throws SQLException {
        Cart cart=new Cart();
         when(cartRepo.getCartByUserId(1)).thenReturn(cart);

         when(cartItemRepo.getCartItems(cart.getId())).thenReturn(List.of());
        customerService.placeOrder();

        verify(orderRepo,never()).createOrder(anyInt(),any(),anyDouble(),any());

    }

    @Test
    void addItemToCart_success() {

        FoodItem foodItem=new FoodItem(1,"Idli",50);
        Cart cart=new Cart(1, customer.getId());

        when(cartRepo.getCartByUserId(customer.getId())).thenReturn(cart);

        customerService.addItemToCart(foodItem,2);

        verify(cartRepo).getCartByUserId(customer.getId());
        verify(cartItemRepo).addItem(any(CartItem.class),eq(1));
    }


}