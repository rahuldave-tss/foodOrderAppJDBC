package com.tss.factory;

import com.tss.entity.*;
import com.tss.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void testCreateAdmin() {
        User user = UserFactory.createUser(Role.ADMIN, "admin1", "Admin", "pass", "admin@test.com", "1111111111");
        assertInstanceOf(Admin.class, user);
        assertEquals("admin1", user.getUserName());
        assertEquals("Admin", user.getName());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void testCreateCustomer() {
        User user = UserFactory.createUser(Role.CUSTOMER, "cust1", "Customer", "pass", "cust@test.com", "2222222222");
        assertInstanceOf(Customer.class, user);
        assertEquals("cust1", user.getUserName());
        assertEquals("Customer", user.getName());
        assertEquals(Role.CUSTOMER, user.getRole());
    }

    @Test
    void testCreateDeliveryPartner() {
        User user = UserFactory.createUser(Role.DELIVERY_PARTNER, "dp1", "Driver", "pass", "dp@test.com", "3333333333");
        assertInstanceOf(DeliveryPartner.class, user);
        assertEquals("dp1", user.getUserName());
        assertEquals("Driver", user.getName());
        assertEquals(Role.DELIVERY_PARTNER, user.getRole());
    }

    @Test
    void testCreateUserSetsCorrectFields() {
        User user = UserFactory.createUser(Role.CUSTOMER, "john123", "John", "secret", "john@mail.com", "9876543210");
        assertEquals("john123", user.getUserName());
        assertEquals("John", user.getName());
        assertEquals("secret", user.getPassword());
        assertEquals("john@mail.com", user.getEmail());
        assertEquals("9876543210", user.getPhoneNumber());
    }
}
