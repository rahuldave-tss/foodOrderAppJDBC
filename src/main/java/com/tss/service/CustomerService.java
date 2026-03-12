package com.tss.service;

import com.tss.enums.OrderStatus;
import com.tss.enums.PaymentType;
import com.tss.exceptions.*;
import com.tss.entity.*;
import com.tss.factory.PaymentFactory;
import com.tss.repository.impl.DPRepo;
import com.tss.repository.impl.DiscountRepo;

import java.util.HashMap;
import java.util.Map;

import static com.tss.utils.Validate.validateInt;

public class CustomerService {
    private DPRepo dpRepo;
    private Customer customer;
    private DiscountRepo discountRepo;
    private DiscountService discountService;
    private DeliveryService deliveryService;

    public CustomerService(DPRepo dpRepo, DiscountRepo discountRepo, User customer, DiscountService discountService, DeliveryService deliveryService) {
        this.dpRepo = dpRepo;
        this.discountRepo=discountRepo;
        this.customer=(Customer) customer;
        this.discountService=discountService;
        this.deliveryService = deliveryService;
    }

    public void placeOrder(){
        Order order=createNewOrderFromCart();
        if(order.getFinalAmount()==0){
            System.out.println("Your cart is empty. Please add items to cart before placing an order.");
            return;
        }
        takePaymentFromCustomer(order);
        try{
            assignDeliveryPartner(order);
        }
        catch(NoDeliveryPartnerAvailableException e){
            System.out.println("Exception: "+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
        try{
            printInvoice(order);
        }
        catch(EmptyCartException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
        customer.addOrderToHistory(order);
        customer.getCart().clear();
    }

    private void takePaymentFromCustomer(Order order) {
        System.out.println();
        System.out.println("Which type of Payment you want to opt?: ");
        System.out.println("1. Cash");
        System.out.println("2. UPI");
        IPaymentService iPaymentService=null;
        boolean flag=false;
        while (true){
            System.out.print("Enter your choice: ");
            int choice=validateInt();
            switch (choice){
                case 1:{
                    iPaymentService= PaymentFactory.getPaymentService(PaymentType.CASH);
                    flag=true;
                    break;
                }
                case 2:{
                    iPaymentService=PaymentFactory.getPaymentService(PaymentType.UPI);
                    flag=true;
                    break;
                }
                default:
                    System.out.println("Enter a valid choice !!");
            }
            if(flag)break;
        }
        iPaymentService.doPayment(order.getFinalAmount());
    }

    private Order createNewOrderFromCart() {
        Cart currentCart=customer.getCart();
        double cartTotal=currentCart.getTotal();
        double discountApplied= discountService.applyMaxDiscount(cartTotal);
        double finalAmount=cartTotal-discountApplied;
        HashMap<FoodItem,OrderItem> items=new HashMap<>(currentCart.getShoppingCart());
        Order order=new Order(items,finalAmount,customer);
        return order;
    }

    public void assignDeliveryPartner(Order order) {
        order.addObserver(customer);
        deliveryService.assignOrder(order);
    }

    private void simulateDelivery(Order order) {

        try{
            Thread.sleep(2000);

            order.setStatus(OrderStatus.PREPARING);
            order.notifyObservers();
            System.out.println();

            Thread.sleep(2000);

            order.setStatus(OrderStatus.ON_THE_WAY);
            order.notifyObservers();
            System.out.println();

            Thread.sleep(2000);

            order.setStatus(OrderStatus.DELIVERED);
            order.notifyObservers();
            System.out.println();

            System.out.println("Order Delivered Successfully !!");
        }
        catch(InterruptedException e){
            System.out.println(e.getClass().getSimpleName());
            System.out.println("Error in simulating delivery: " + e.getMessage());
        }

    }

    private void printInvoice(Order order) {

        Cart cart = customer.getCart();

        if (cart == null || cart.getShoppingCart().isEmpty()) {
            throw new EmptyCartException();
        }

        Map<FoodItem, OrderItem> items = cart.getShoppingCart();

        System.out.println("\n==================================================================");
        System.out.println("                             INVOICE                          ");
        System.out.println("==================================================================");
        System.out.println("Order ID       : " + order.getOrderId());
        System.out.println("Order Status   : " + order.getStatus());
        System.out.println("Delivery Agent : " +
                (order.getDeliveryPartner() != null
                        ? order.getDeliveryPartner().getName()
                        : "Not Assigned"));
        System.out.println("Delivery Address: " +
                (order.getCustomer().getCustomerAddress() != null
                        ? order.getCustomer().getCustomerAddress()
                        : "Not Provided"));
        System.out.println("==================================================================");

        System.out.printf("%-5s %-22s %-8s %-10s %-12s%n",
                "No", "Item", "Qty", "Price", "Subtotal");

        System.out.println("------------------------------------------------------------------");

        double grandTotal = 0;
        int index = 1;

        for (OrderItem item : items.values()) {

            String name = item.getFoodItem().getName();
            double price = item.getFoodItem().getPrice();
            int qty = item.getQuantity();
            double subtotal = price * qty;

            grandTotal += subtotal;

            System.out.printf("%-5d %-22s %-8d %-10.2f %-12.2f%n",
                    index++, name, qty, price, subtotal);
        }

        System.out.println("------------------------------------------------------------------");

        double discount = discountService.applyMaxDiscount(cart.getTotal());
        double finalAmount = order.getFinalAmount();

        System.out.printf("%-48s %-12.2f%n", "Grand Total:", grandTotal);
        System.out.printf("%-48s %-12.2f%n", "Discount Applied:", discount);
        System.out.printf("%-48s %-12.2f%n", "Final Payable Amount:", finalAmount);

        System.out.println("==================================================================");
        System.out.println("              Thank You For Ordering With Us !!");
        System.out.println("==================================================================\n");
    }

}
