package com.tss.service;

import com.tss.enums.OrderStatus;
import com.tss.enums.PaymentType;
import com.tss.exceptions.*;
import com.tss.entity.*;
import com.tss.factory.PaymentFactory;
import com.tss.repository.impl.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tss.utils.Validate.validateInt;

public class CustomerService {
    private DPRepo dpRepo;
    private Customer customer;
    private DiscountRepo discountRepo;
    private DiscountService discountService;
    private DeliveryService deliveryService;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;
    private PaymentRepo paymentRepo;
    private CartRepo cartRepo;
    private CartItemRepo cartItemRepo;

    public CustomerService(DPRepo dpRepo, DiscountRepo discountRepo, User customer,
                           DiscountService discountService, DeliveryService deliveryService,
                           OrderRepo orderRepo, OrderItemRepo orderItemRepo, PaymentRepo paymentRepo,
                           CartRepo cartRepo, CartItemRepo cartItemRepo) {
        this.dpRepo = dpRepo;
        this.discountRepo = discountRepo;
        this.customer = (Customer) customer;
        this.discountService = discountService;
        this.deliveryService = deliveryService;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentRepo = paymentRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
    }

    public void placeOrder() {
        // Get or create cart from DB
        Cart cart = cartRepo.getCartByUserId(customer.getId());
        if (cart == null) {
            System.out.println("Your cart is empty. Please add items to cart before placing an order.");
            return;
        }

        List<CartItem> cartItems = cartItemRepo.getCartItems(cart.getId());
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty. Please add items to cart before placing an order.");
            return;
        }

        // Calculate cart total
        double cartTotal = 0;
        for (CartItem ci : cartItems) {
            cartTotal += ci.getTotal();
        }

        // Apply discount
        String discountWithId = discountService.applyMaxDiscount(cartTotal);

        String[] arr = discountWithId.split("-");
        double discountApplied=Double.parseDouble(arr[0]);
        int discountId=Integer.parseInt(arr[1]);
        double finalAmount = cartTotal - discountApplied;

        // Take payment
        PaymentType paymentType = takePaymentFromCustomer(finalAmount);

        // Create order in DB
        int orderId = orderRepo.createOrder(customer.getId(), discountId, finalAmount, OrderStatus.CREATED);

        // Save order items in DB
        for (CartItem ci : cartItems) {
            orderItemRepo.addOrderItem(orderId, ci.getFoodItem().getId(), ci.getQuantity(), ci.getTotal());
        }

        // Save payment in DB
        paymentRepo.savePayment(orderId, paymentType, finalAmount);

        // Assign delivery partner
        try {
            deliveryService.assignOrderById(orderId);
        } catch (NoDeliveryPartnerAvailableException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }

        // Print invoice
        printInvoice(orderId, cartItems, cartTotal, discountApplied, finalAmount,paymentType);

        // Clear cart in DB
        cartItemRepo.clearCart(cart.getId());
    }

    private PaymentType takePaymentFromCustomer(double amount) {
        System.out.println();
        System.out.println("Which type of Payment you want to opt?: ");
        System.out.println("1. Cash");
        System.out.println("2. UPI");
        IPaymentService iPaymentService = null;
        PaymentType paymentType = null;
        boolean flag = false;
        while (true) {
            System.out.print("Enter your choice: ");
            int choice = validateInt();
            switch (choice) {
                case 1: {
                    iPaymentService = PaymentFactory.getPaymentService(PaymentType.CASH);
                    paymentType = PaymentType.CASH;
                    flag = true;
                    break;
                }
                case 2: {
                    iPaymentService = PaymentFactory.getPaymentService(PaymentType.UPI);
                    paymentType = PaymentType.UPI;
                    flag = true;
                    break;
                }
                default:
                    System.out.println("Enter a valid choice !!");
            }
            if (flag) break;
        }
        iPaymentService.doPayment(amount);
        return paymentType;
    }

    private void printInvoice(int orderId, List<CartItem> cartItems,
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

    // Used by CustomerController to add items to cart in DB
    public void addItemToCart(FoodItem foodItem, int quantity) {
        Cart cart = cartRepo.getCartByUserId(customer.getId());
        if (cart == null) {
            cart = cartRepo.createCart(customer.getId());
        }
        CartItem cartItem = new CartItem(foodItem, quantity);
        cartItemRepo.addItem(cartItem, cart.getId());
    }

    // Used by CustomerController to get cart items from DB
    public List<CartItem> getCartItems() {
        Cart cart = cartRepo.getCartByUserId(customer.getId());
        if (cart == null) {
            return List.of();
        }
        return cartItemRepo.getCartItems(cart.getId());
    }

    // Used by CustomerController to remove items from cart in DB
    public void removeItemFromCart(int foodItemId, int qtyToRemove) {
        Cart cart = cartRepo.getCartByUserId(customer.getId());
        if (cart == null) return;

        List<CartItem> items = cartItemRepo.getCartItems(cart.getId());
        for (CartItem ci : items) {
            if (ci.getFoodItem().getId() == foodItemId) {
                int currentQty = ci.getQuantity();
                if (qtyToRemove <= 0) {
                    System.out.println("Invalid quantity!");
                } else if (qtyToRemove < currentQty) {
                    cartItemRepo.updateQuantity(cart.getId(), foodItemId, currentQty - qtyToRemove);
                    System.out.println("Quantity updated successfully.");
                } else if (qtyToRemove == currentQty) {
                    cartItemRepo.removeItem(cart.getId(), foodItemId);
                    System.out.println("Item removed completely from cart.");
                } else {
                    System.out.println("You cannot remove more than existing quantity!");
                }
                return;
            }
        }
        System.out.println("Item not found in cart.");
    }

    // Get order history from DB
    public List<Order> getOrderHistory() {
        return orderRepo.getOrdersByCustomerId(customer.getId());
    }

    // Display cart summary from DB
    public void displayCartSummary() {
        List<CartItem> items = getCartItems();
        if (items.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }
        System.out.println("\n------------------- CART SUMMARY -------------------");
        System.out.printf("%-5s %-22s %-8s %-10s %-12s%n", "No", "Item", "Qty", "Price", "Subtotal");
        System.out.println("----------------------------------------------------");
        double total = 0;
        int index = 1;
        for (CartItem ci : items) {
            double subtotal = ci.getTotal();
            total += subtotal;
            System.out.printf("%-5d %-22s %-8d %-10.2f %-12.2f%n",
                    index++, ci.getFoodItem().getName(), ci.getQuantity(),
                    ci.getFoodItem().getPrice(), subtotal);
        }
        System.out.println("----------------------------------------------------");
        System.out.printf("%-48s %-12.2f%n", "Total:", total);
        System.out.println("----------------------------------------------------\n");
    }
}
