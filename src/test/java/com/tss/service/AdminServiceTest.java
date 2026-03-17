package com.tss.service;

import com.tss.entity.*;
import com.tss.enums.OrderStatus;
import com.tss.exceptions.EmptyMenuException;
import com.tss.repository.impl.*;

import java.sql.SQLException;
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
class AdminServiceTest {

    @Mock private MenuRepo menuRepo;
    @Mock private DPRepo dpRepo;
    @Mock private UserRepo userRepo;
    @Mock private DiscountRepo discountRepo;
    @Mock private CustomerRepo customerRepo;
    @Mock private OrderRepo orderRepo;
    @Mock private DeliveryService deliveryService;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(menuRepo, dpRepo, userRepo, discountRepo, customerRepo, orderRepo, deliveryService);
    }

    @Test
    void testAddItem() {
        when(menuRepo.addItem(any(FoodItem.class))).thenReturn(1);

        int result = adminService.addItem("Burger", 150.0);

        assertEquals(1, result);
        verify(menuRepo, times(1)).addItem(any(FoodItem.class));
    }

    @Test
    void testRemoveItemSuccess() {
        FoodItem food = new FoodItem(1, "Burger", 150.0);
        List<FoodItem> menu = List.of(food);

        when(menuRepo.getMenu()).thenReturn(menu);
        when(menuRepo.getFoodItemById(1)).thenReturn(food);

        boolean result = adminService.removeItem(1);

        assertTrue(result);
        verify(menuRepo, times(1)).removeItem(food);
    }

    @Test
    void testRemoveItemNotFound() {
        List<FoodItem> menu = List.of(new FoodItem(1, "Burger", 150.0));
        when(menuRepo.getMenu()).thenReturn(menu);
        when(menuRepo.getFoodItemById(99)).thenReturn(null);

        boolean result = adminService.removeItem(99);

        assertFalse(result);
        verify(menuRepo, never()).removeItem(any());
    }

    @Test
    void testRemoveItemEmptyMenuThrowsException() {
        when(menuRepo.getMenu()).thenReturn(Collections.emptyList());

        assertThrows(EmptyMenuException.class, () -> adminService.removeItem(1));
    }

    @Test
    void testGetAllItems() {
        List<FoodItem> menu = List.of(
                new FoodItem(1, "Burger", 150.0),
                new FoodItem(2, "Pizza", 250.0)
        );
        when(menuRepo.getMenu()).thenReturn(menu);

        List<FoodItem> result = adminService.getAllItems();

        assertEquals(2, result.size());
        verify(menuRepo, times(1)).getMenu();
    }

    @Test
    void testAddDiscount() {
        DiscountStrategy discount = new Discount("Sale", 500.0, 10.0);
        when(discountRepo.addDiscount(discount)).thenReturn(1);

        int result = adminService.addDiscount(discount);

        assertEquals(1, result);
        verify(discountRepo, times(1)).addDiscount(discount);
    }

    @Test
    void testRemoveDiscountSuccess() {
        when(discountRepo.removeDiscount(1)).thenReturn(true);

        assertTrue(adminService.removeDiscount(1));
    }

    @Test
    void testRemoveDiscountNotFound() {
        when(discountRepo.removeDiscount(99)).thenReturn(false);

        assertFalse(adminService.removeDiscount(99));
    }

    @Test
    void testFindDiscountByAmount() {
        when(discountRepo.findDiscountByAmount(500.0)).thenReturn(true);

        assertTrue(adminService.findDiscountByAmount(500.0));
    }

    @Test
    void testGetAllDiscounts() {
        List<DiscountStrategy> discounts = List.of(new Discount(1, "Sale", 500.0, 10.0));
        when(discountRepo.getAvailableDiscounts()).thenReturn(discounts);

        List<DiscountStrategy> result = adminService.getAllDiscounts();

        assertEquals(1, result.size());
    }

    @Test
    void testAddDeliveryPartnerSuccessNoPendingOrders() {
        DeliveryPartner partner = new DeliveryPartner("dp1", "Driver", "pass", "dp@test.com", "1234567890");
        when(userRepo.addUser(partner)).thenReturn(1);
        when(orderRepo.getPendingOrders()).thenReturn(Collections.emptyList());

        adminService.addDeliveryPartner(partner);

        assertEquals(1, partner.getId());
        verify(dpRepo, times(1)).addPartner(partner);
        verify(deliveryService, never()).assignNextPendingOrder(any());
    }

    @Test
    void testAddDeliveryPartnerWithPendingOrders() {
        DeliveryPartner partner = new DeliveryPartner("dp1", "Driver", "pass", "dp@test.com", "1234567890");
        Customer customer = new Customer(1, "cust", "Cust", "pass", "c@test.com", "1111111111");
        Order pendingOrder = new Order(1, new ArrayList<>(), 100.0, customer, null);

        when(userRepo.addUser(partner)).thenReturn(2);
        when(orderRepo.getPendingOrders()).thenReturn(List.of(pendingOrder));

        adminService.addDeliveryPartner(partner);

        verify(deliveryService, times(1)).assignNextPendingOrder(partner);
    }

    @Test
    void testAddDeliveryPartnerUserCreationFails() {
        DeliveryPartner partner = new DeliveryPartner("dp1", "Driver", "pass", "dp@test.com", "1234567890");
        when(userRepo.addUser(partner)).thenReturn(-1);

        adminService.addDeliveryPartner(partner);

        verify(dpRepo, never()).addPartner(any());
    }

    @Test
    void testRemoveDeliveryPartnerNotFound() throws SQLException {
        when(dpRepo.getDeliveryPartnerById(99)).thenReturn(null);

        adminService.removeDeliveryPartner(99);

        verify(dpRepo, never()).removePartner(any());
    }

    @Test
    void testGetAllDeliveryPartners() {
        List<DeliveryPartner> partners = List.of(
                new DeliveryPartner(1, "dp1", "Driver1", "pass", "dp1@test.com", "1111111111")
        );
        when(dpRepo.getDeliveryPartners()).thenReturn(partners);

        List<DeliveryPartner> result = adminService.getAllDeliveryPartners();

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = List.of(
                new Customer(1, "cust1", "Customer1", "pass", "c1@test.com", "1111111111")
        );
        when(customerRepo.getCustomerList()).thenReturn(customers);

        List<Customer> result = adminService.getAllCustomers();

        assertEquals(1, result.size());
    }
}