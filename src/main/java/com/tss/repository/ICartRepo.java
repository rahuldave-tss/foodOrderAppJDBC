package com.tss.repository;

import com.tss.entity.Cart;
import com.tss.entity.Customer;

public interface ICartRepo {
    Cart createCart(int customerId);
    Cart getCartByUserId(int userId);
}
