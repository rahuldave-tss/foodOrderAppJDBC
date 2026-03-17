package com.tss.repository;

import com.tss.entity.Customer;
import com.tss.entity.User;

import java.util.List;

public interface ICustomerRepo {
    void addCustomer(Customer customer,int customerId);
    void removeCustomer(Customer customer);
    List<Customer> getCustomerList();
    Customer getCustomerById(int customerId);
}
