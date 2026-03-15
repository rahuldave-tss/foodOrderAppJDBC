package com.tss.controller;

import com.tss.entity.*;
import com.tss.repository.impl.MenuRepo;
import com.tss.service.CustomerService;

import java.util.List;

import static com.tss.utils.Display.displayMenu;
import static com.tss.utils.Validate.validateInt;
import static com.tss.utils.Validate.validateString;

public class CustomerController {
    private CustomerService customerService;
    private MenuRepo menuRepo;

    public CustomerController(CustomerService customerService, MenuRepo menuRepo) {
        this.customerService = customerService;
        this.menuRepo = menuRepo;
    }

    public void start() {
        while (true) {
            System.out.println("\n================================================");
            System.out.println("                CUSTOMER DASHBOARD              ");
            System.out.println("================================================");
            System.out.println(" Please select an option:");
            System.out.println("------------------------------------------------");
            System.out.println("  1. Add Items to Cart");
            System.out.println("  2. Remove Items from Cart");
            System.out.println("  3. View Cart Summary");
            System.out.println("  4. Place Order");
            System.out.println("  5. View Order History");
            System.out.println("  6. Logout");
            System.out.println("------------------------------------------------");
            System.out.print(" Enter your choice (1-6): ");

            int choice = validateInt();
            System.out.println();

            switch (choice) {

                case 1: {
                    addItemsToCart();
                    break;
                }

                case 2: {
                    removeItemsFromCart();
                    break;
                }

                case 3: {
                    viewCartSummary();
                    break;
                }

                case 4: {
                    placeOrder();
                    break;
                }

                case 5: {
                    viewOrderHistory();
                    break;
                }

                case 6: {
                    System.out.println("Logging out...");
                    System.out.println("================================================\n");
                    return;
                }

                default: {
                    System.out.println("Invalid choice! Please enter a number between 1 and 6.");
                }
            }
        }
    }

    private void viewOrderHistory() {
        List<Order> orderHistory = customerService.getOrderHistory();
        if (orderHistory.isEmpty()) {
            System.out.println("No orders placed yet.");
            return;
        }
        System.out.println(displayOrderHistoryHeader());
        for (Order o : orderHistory) {
            System.out.println(o.getCustomerHistoryRow());
        }
    }

    public static String displayOrderHistoryHeader() {
        return "+----------+--------------+-----------------+--------------------+--------------------------------+\n" +
                "| Order ID | Final Amount | Status          | Delivery Partner   | Items                          |\n" +
                "+----------+--------------+-----------------+--------------------+--------------------------------+";
    }

    private void placeOrder() {
        customerService.placeOrder();
    }

    private void viewCartSummary() {
        customerService.displayCartSummary();
    }

    private void removeItemsFromCart() {
        List<CartItem> items = customerService.getCartItems();
        if (items.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }

        // Display cart
        System.out.println("\n------------------- YOUR CART -------------------");
        System.out.printf("%-5s %-22s %-8s %-10s%n", "ID", "Item", "Qty", "Price");
        System.out.println("-------------------------------------------------");
        for (CartItem ci : items) {
            System.out.printf("%-5d %-22s %-8d %-10.2f%n",
                    ci.getFoodItem().getId(), ci.getFoodItem().getName(),
                    ci.getQuantity(), ci.getFoodItem().getPrice());
        }
        System.out.println("-------------------------------------------------");

        System.out.print("Enter the FoodItem ID to remove: ");
        int id = validateInt();

        // Find the item in the cart
        CartItem itemToModify = null;
        for (CartItem ci : items) {
            if (ci.getFoodItem().getId() == id) {
                itemToModify = ci;
                break;
            }
        }

        if (itemToModify != null) {
            int currentQty = itemToModify.getQuantity();
            System.out.println("Current quantity in cart: " + currentQty);
            System.out.print("Enter quantity to remove: ");
            int qtyToRemove = validateInt();
            customerService.removeItemFromCart(id, qtyToRemove);
        } else {
            System.out.println("Item not found in cart.");
        }

        while (true) {
            System.out.print("Do you want to remove more items? (y/n): ");
            String s = validateString();

            if (s.equalsIgnoreCase("y")) {
                removeItemsFromCart();
                break;
            } else if (s.equalsIgnoreCase("n")) {
                break;
            } else {
                System.out.println("Invalid input !!");
            }
        }
    }

    private void addItemsToCart() {
        displayMenu(menuRepo.getMenu());
        System.out.print("Enter the FoodItem ID to add: ");
        int id = validateInt();
        FoodItem foodItem = menuRepo.getFoodItemById(id);
        if (foodItem == null) {
            System.out.println("Invalid ID...");
            return;
        }
        System.out.print("Enter quantity: ");
        int quantity = validateInt();

        customerService.addItemToCart(foodItem, quantity);
        System.out.println("Item added to cart!");

        while (true) {
            System.out.print("Do you want to add more items?(y/n) :");
            String s = validateString();
            if (s.equalsIgnoreCase("y")) {
                addItemsToCart();
                break;
            } else if (s.equalsIgnoreCase("n")) {
                break;
            } else {
                System.out.println("Invalid input !!");
            }
        }
    }
}
