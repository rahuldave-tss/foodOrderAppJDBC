package com.tss.service;

import com.tss.entity.*;
import com.tss.enums.OrderStatus;
import com.tss.repository.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private OrderRepo orderRepo;
    @Mock private OrderItemRepo orderItemRepo;
    @Mock private PaymentRepo paymentRepo;
    @Mock private CartRepo cartRepo;
    @Mock private CartItemRepo cartItemRepo;
    @Mock private DiscountService discountService;
    @Mock private DeliveryService deliveryService;

    private CustomerService customerService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(1, "r123", "Rahul", "123", "rahul@gmail.com", "1234567890");
        customerService = new CustomerService(
                customer, discountService, deliveryService,
                orderRepo, orderItemRepo, paymentRepo,
                cartRepo, cartItemRepo
        );
    }

    @Test
    void testAddItemToCartExistingCart() {
        Cart cart = new Cart(10, 1);
        FoodItem food = new FoodItem(1, "Burger", 150.0);

        when(cartRepo.getCartByUserId(1)).thenReturn(cart);

        customerService.addItemToCart(food, 2);

        verify(cartRepo, never()).createCart(anyInt());
        verify(cartItemRepo, times(1)).addItem(any(CartItem.class), eq(10));
    }

    @Test
    void testAddItemToCartNewCart() {
        Cart newCart = new Cart(20, 1);
        FoodItem food = new FoodItem(1, "Pizza", 250.0);

        when(cartRepo.getCartByUserId(1)).thenReturn(null);
        when(cartRepo.createCart(1)).thenReturn(newCart);

        customerService.addItemToCart(food, 1);

        verify(cartRepo, times(1)).createCart(1);
        verify(cartItemRepo, times(1)).addItem(any(CartItem.class), eq(20));
    }

    @Test
    void testRemoveItemFromCartPartialRemoval() {
        Cart cart = new Cart(10, 1);
        FoodItem food = new FoodItem(5, "Burger", 150.0);
        CartItem cartItem = new CartItem(food, 3);

        when(cartRepo.getCartByUserId(1)).thenReturn(cart);
        when(cartItemRepo.getCartItems(10)).thenReturn(List.of(cartItem));

        customerService.removeItemFromCart(5, 1);

        verify(cartItemRepo, times(1)).updateQuantity(10, 5, 2);
    }

    @Test
    void testRemoveItemFromCartFullRemoval() {
        Cart cart = new Cart(10, 1);
        FoodItem food = new FoodItem(5, "Burger", 150.0);
        CartItem cartItem = new CartItem(food, 2);

        when(cartRepo.getCartByUserId(1)).thenReturn(cart);
        when(cartItemRepo.getCartItems(10)).thenReturn(List.of(cartItem));

        customerService.removeItemFromCart(5, 2);

        verify(cartItemRepo, times(1)).removeItem(10, 5);
    }

    @Test
    void testRemoveItemFromCartExcessQuantity() {
        Cart cart = new Cart(10, 1);
        FoodItem food = new FoodItem(5, "Burger", 150.0);
        CartItem cartItem = new CartItem(food, 2);

        when(cartRepo.getCartByUserId(1)).thenReturn(cart);
        when(cartItemRepo.getCartItems(10)).thenReturn(List.of(cartItem));

        customerService.removeItemFromCart(5, 5);

        verify(cartItemRepo, never()).updateQuantity(anyInt(), anyInt(), anyInt());
        verify(cartItemRepo, never()).removeItem(anyInt(), anyInt());
    }

    @Test
    void testRemoveItemFromCartInvalidQuantity() {
        Cart cart = new Cart(10, 1);
        FoodItem food = new FoodItem(5, "Burger", 150.0);
        CartItem cartItem = new CartItem(food, 2);

        when(cartRepo.getCartByUserId(1)).thenReturn(cart);
        when(cartItemRepo.getCartItems(10)).thenReturn(List.of(cartItem));

        customerService.removeItemFromCart(5, 0);

        verify(cartItemRepo, never()).updateQuantity(anyInt(), anyInt(), anyInt());
        verify(cartItemRepo, never()).removeItem(anyInt(), anyInt());
    }

    @Test
    void testRemoveItemFromCartItemNotFound() {
        Cart cart = new Cart(10, 1);
        FoodItem food = new FoodItem(5, "Burger", 150.0);
        CartItem cartItem = new CartItem(food, 2);

        when(cartRepo.getCartByUserId(1)).thenReturn(cart);
        when(cartItemRepo.getCartItems(10)).thenReturn(List.of(cartItem));

        customerService.removeItemFromCart(99, 1);

        verify(cartItemRepo, never()).updateQuantity(anyInt(), anyInt(), anyInt());
        verify(cartItemRepo, never()).removeItem(anyInt(), anyInt());
    }

    @Test
    void testRemoveItemFromCartNullCart() {
        when(cartRepo.getCartByUserId(1)).thenReturn(null);

        customerService.removeItemFromCart(5, 1);

        verify(cartItemRepo, never()).getCartItems(anyInt());
    }

    @Test
    void testGetOrderHistory() {
        Order order = new Order(1, new ArrayList<>(), 500.0, customer, null);
        when(orderRepo.getOrdersByCustomerId(1)).thenReturn(List.of(order));

        List<Order> result = customerService.getOrderHistory();

        assertEquals(1, result.size());
        verify(orderRepo, times(1)).getOrdersByCustomerId(1);
    }

    @Test
    void testGetOrderHistoryEmpty() {
        when(orderRepo.getOrdersByCustomerId(1)).thenReturn(Collections.emptyList());

        List<Order> result = customerService.getOrderHistory();

        assertTrue(result.isEmpty());
    }
}