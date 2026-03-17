package com.tss.service;

import com.tss.entity.*;
import com.tss.enums.OrderStatus;
import com.tss.repository.impl.DPRepo;
import com.tss.repository.impl.OrderItemRepo;
import com.tss.repository.impl.OrderRepo;
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
class DeliveryServiceTest {

    @Mock private DPRepo dpRepo;
    @Mock private OrderRepo orderRepo;
    @Mock private OrderItemRepo orderItemRepo;

    private DeliveryService deliveryService;

    private DeliveryPartner availablePartner;
    private DeliveryPartner busyPartner;
    private Customer customer;

    @BeforeEach
    void setUp() {
        deliveryService = new DeliveryService(dpRepo, orderRepo, orderItemRepo);

        availablePartner = new DeliveryPartner(1, "dp1", "Driver1", "pass", "dp1@test.com", "1111111111");
        availablePartner.setAvailable(true);

        busyPartner = new DeliveryPartner(2, "dp2", "Driver2", "pass", "dp2@test.com", "2222222222");
        busyPartner.setAvailable(false);

        customer = new Customer(1, "cust1", "John", "pass", "john@test.com", "9999999999");
    }

    @Test
    void testAssignOrderByIdAvailablePartner() {
        when(dpRepo.getDeliveryPartners()).thenReturn(List.of(availablePartner));

        deliveryService.assignOrderById(10);

        verify(dpRepo, times(1)).updateAvailability(1, false);
        verify(orderRepo, times(1)).assignDeliveryPartner(10, 1);
    }

    @Test
    void testAssignOrderByIdNoAvailablePartner() {
        when(dpRepo.getDeliveryPartners()).thenReturn(List.of(busyPartner));

        deliveryService.assignOrderById(10);

        verify(orderRepo, times(1)).updateOrderStatus(10, OrderStatus.PENDING);
    }

    @Test
    void testAssignOrderByIdNoPartnersAtAll() {
        when(dpRepo.getDeliveryPartners()).thenReturn(Collections.emptyList());

        deliveryService.assignOrderById(10);

        verify(orderRepo, times(1)).updateOrderStatus(10, OrderStatus.PENDING);
    }

    @Test
    void testAssignOrderByIdPicksFirstAvailable() {
        when(dpRepo.getDeliveryPartners()).thenReturn(List.of(busyPartner, availablePartner));

        deliveryService.assignOrderById(10);

        verify(orderRepo, times(1)).assignDeliveryPartner(10, availablePartner.getId());
        verify(dpRepo, times(1)).updateAvailability(availablePartner.getId(), false);
    }

    @Test
    void testCompleteCurrentOrderSuccess() {
        Order activeOrder = new Order(5, new ArrayList<>(), 200.0, customer, availablePartner);
        activeOrder.setStatus(OrderStatus.ASSIGNED);

        when(orderRepo.getOrdersByDeliveryPartnerId(1)).thenReturn(List.of(activeOrder));
        when(orderRepo.getPendingOrders()).thenReturn(Collections.emptyList());

        deliveryService.completeCurrentOrder(availablePartner);

        verify(orderRepo, times(1)).updateOrderStatus(5, OrderStatus.DELIVERED);
        verify(dpRepo, times(1)).updateAvailability(1, true);
    }

    @Test
    void testCompleteCurrentOrderNoActiveOrder() {
        Order delivered = new Order(5, new ArrayList<>(), 200.0, customer, availablePartner);
        delivered.setStatus(OrderStatus.DELIVERED);

        when(orderRepo.getOrdersByDeliveryPartnerId(1)).thenReturn(List.of(delivered));

        deliveryService.completeCurrentOrder(availablePartner);

        verify(orderRepo, never()).updateOrderStatus(anyInt(), eq(OrderStatus.DELIVERED));
        verify(dpRepo, never()).updateAvailability(anyInt(), anyBoolean());
    }

    @Test
    void testCompleteCurrentOrderNoOrders() {
        when(orderRepo.getOrdersByDeliveryPartnerId(1)).thenReturn(Collections.emptyList());

        deliveryService.completeCurrentOrder(availablePartner);

        verify(orderRepo, never()).updateOrderStatus(anyInt(), any());
    }

    @Test
    void testCompleteCurrentOrderAssignsNextPending() {
        Order activeOrder = new Order(5, new ArrayList<>(), 200.0, customer, availablePartner);
        activeOrder.setStatus(OrderStatus.ASSIGNED);

        Order pendingOrder = new Order(6, new ArrayList<>(), 150.0, customer, null);
        pendingOrder.setStatus(OrderStatus.PENDING);

        when(orderRepo.getOrdersByDeliveryPartnerId(1)).thenReturn(List.of(activeOrder));
        when(orderRepo.getPendingOrders()).thenReturn(List.of(pendingOrder));

        deliveryService.completeCurrentOrder(availablePartner);

        verify(orderRepo, times(1)).updateOrderStatus(5, OrderStatus.DELIVERED);
        verify(dpRepo, times(1)).updateAvailability(1, true);
        // assignNextPendingOrder should then assign pending order
        verify(dpRepo, times(1)).updateAvailability(1, false);
        verify(orderRepo, times(1)).assignDeliveryPartner(6, 1);
    }


    @Test
    void testAssignNextPendingOrderWithPending() {
        Order pendingOrder = new Order(7, new ArrayList<>(), 100.0, customer, null);
        when(orderRepo.getPendingOrders()).thenReturn(List.of(pendingOrder));

        deliveryService.assignNextPendingOrder(availablePartner);

        verify(dpRepo, times(1)).updateAvailability(1, false);
        verify(orderRepo, times(1)).assignDeliveryPartner(7, 1);
    }

    @Test
    void testAssignNextPendingOrderNoPending() {
        when(orderRepo.getPendingOrders()).thenReturn(Collections.emptyList());

        deliveryService.assignNextPendingOrder(availablePartner);

        verify(dpRepo, never()).updateAvailability(anyInt(), anyBoolean());
        verify(orderRepo, never()).assignDeliveryPartner(anyInt(), anyInt());
    }
}
