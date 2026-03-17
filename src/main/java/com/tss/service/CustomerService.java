package com.tss.service;

import com.tss.config.DBConnection;
import com.tss.enums.OrderStatus;
import com.tss.enums.PaymentType;
import com.tss.exceptions.*;
import com.tss.entity.*;
import com.tss.factory.PaymentFactory;
import com.tss.repository.impl.*;
import com.tss.utils.Display;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.tss.utils.Validate.validateInt;

public class CustomerService {
    private Customer customer;
    private DiscountService discountService;
    private DeliveryService deliveryService;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;
    private PaymentRepo paymentRepo;
    private CartRepo cartRepo;
    private CartItemRepo cartItemRepo;
    private Connection connection;

    public CustomerService(User customer,
                           DiscountService discountService, DeliveryService deliveryService,
                           OrderRepo orderRepo, OrderItemRepo orderItemRepo, PaymentRepo paymentRepo,
                           CartRepo cartRepo, CartItemRepo cartItemRepo) {
        this.customer = (Customer) customer;
        this.discountService = discountService;
        this.deliveryService = deliveryService;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentRepo = paymentRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.connection=DBConnection.connect();
    }

    public void placeOrder() throws SQLException {
        try{
            connection.setAutoCommit(false);

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

            double cartTotal = 0;
            for (CartItem ci : cartItems) {
                cartTotal += ci.getTotal();
            }

            DiscountStrategy discount = discountService.applyMaxDiscount(cartTotal);

            int discountId=-1;
            double discountApplied=0;
            double finalAmount = cartTotal;

            if(discount!=null){
                discountId=discount.getDiscountId();
                discountApplied=(cartTotal*discount.getDiscountPercentage())/100;
                finalAmount-=discountApplied;
            }

            PaymentType paymentType = takePaymentFromCustomer(finalAmount);

            int orderId = orderRepo.createOrder(customer.getId(), discountId==-1?null:discountId, finalAmount, OrderStatus.CREATED);

            for (CartItem ci : cartItems) {
                orderItemRepo.addOrderItem(orderId, ci.getFoodItem().getId(), ci.getQuantity(), ci.getTotal());
            }

            paymentRepo.savePayment(orderId, paymentType, finalAmount);

            try {
                deliveryService.assignOrderById(orderId);
            } catch (NoDeliveryPartnerAvailableException e) {
                System.out.println("Exception: " + e.getClass().getSimpleName());
                System.out.println(e.getMessage());
            }

            Display.printInvoice(orderRepo,customer,orderId, cartItems, cartTotal, discountApplied, finalAmount,paymentType);

            cartItemRepo.clearCart(cart.getId());

            connection.commit();
        }
        catch (Exception e){
            connection.rollback();
            System.out.println("Error occurred in transaction ");
        }
        finally {
            connection.setAutoCommit(true);
        }

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

    public void addItemToCart(FoodItem foodItem, int quantity) {
        Cart cart = cartRepo.getCartByUserId(customer.getId());
        if (cart == null) {
            cart = cartRepo.createCart(customer.getId());
        }
        CartItem cartItem = new CartItem(foodItem, quantity);
        cartItemRepo.addItem(cartItem, cart.getId());
    }

    public List<CartItem> getCartItems() {
        Cart cart = cartRepo.getCartByUserId(customer.getId());
        if (cart == null) {
            return List.of();
        }
        return cartItemRepo.getCartItems(cart.getId());
    }

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

    public List<Order> getOrderHistory() {
        return orderRepo.getOrdersByCustomerId(customer.getId());
    }

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
