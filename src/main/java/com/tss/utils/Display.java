package com.tss.utils;

import com.tss.entity.CartItem;
import com.tss.entity.Customer;
import com.tss.entity.FoodItem;
import com.tss.entity.Order;
import com.tss.enums.OrderStatus;
import com.tss.enums.PaymentType;
import com.tss.repository.impl.CustomerRepo;
import com.tss.repository.impl.OrderRepo;

import java.util.List;

public class Display {
    public static void displayFoodItemHeader(){
        System.out.printf("%-10s %-20s %-10s%n", "ID", "Name", "Price");
        System.out.println("----------------------------------------------");
    }

    public static void displayMenu(List<FoodItem> items){
        System.out.println("\n************ FOOD MENU ************");
        displayFoodItemHeader();
        items.forEach(System.out::println);
    }

    public static void printInvoice(OrderRepo orderRepo, Customer customer, int orderId, List<CartItem> cartItems,
                                    double cartTotal, double discount,
                                    double finalAmount, PaymentType paymentType) {

        Order order = orderRepo.getOrderById(orderId);

        System.out.println("\n==============================================================");
        System.out.println("                        FOOD ORDER INVOICE");
        System.out.println("==============================================================");

        System.out.println("Order ID         : " + orderId);
        System.out.println("Order Status     : " + (order != null ? order.getStatus() : OrderStatus.CREATED));
        System.out.println("Payment Method   : " + paymentType);

        if (order != null && order.getDeliveryPartner() != null) {
            System.out.println("Delivery Partner : " + order.getDeliveryPartner().getName());
        } else {
            System.out.println("Delivery Partner : Not Assigned");
        }

        System.out.println("Delivery Address : " +
                (customer.getCustomerAddress() != null
                        ? customer.getCustomerAddress()
                        : "Not Provided"));

        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-5s %-22s %-8s %-10s %-12s%n",
                "No", "Item", "Qty", "Price", "Subtotal");
        System.out.println("--------------------------------------------------------------");

        int index = 1;

        for (CartItem item : cartItems) {

            System.out.printf("%-5d %-22s %-8d %-10.2f %-12.2f%n",
                    index++,
                    item.getFoodItem().getName(),
                    item.getQuantity(),
                    item.getFoodItem().getPrice(),
                    item.getTotal());
        }

        System.out.println("--------------------------------------------------------------");

        System.out.printf("%-40s %10.2f%n", "Cart Total:", cartTotal);
        System.out.printf("%-40s %10.2f%n", "Discount Applied:", discount);
        System.out.printf("%-40s %10.2f%n", "Final Payable Amount:", finalAmount);

        System.out.println("==============================================================");
        System.out.println("               Thank You For Ordering With Us!");
        System.out.println("==============================================================\n");
    }
}
