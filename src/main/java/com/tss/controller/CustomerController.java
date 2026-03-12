package com.tss.controller;

import com.tss.exceptions.*;
import com.tss.entity.*;
import com.tss.repository.impl.MenuRepo;
import com.tss.service.CustomerService;
import com.tss.utils.Display;

import java.util.List;
import java.util.Map;

import static com.tss.utils.Validate.validateInt;
import static com.tss.utils.Validate.validateString;

public class CustomerController {
    private CustomerService customerService;
    private MenuRepo menuRepo;
    private Customer customer;

    public CustomerController(CustomerService customerService, MenuRepo menuRepo, User customer) {
        this.customerService = customerService;
        this.menuRepo=menuRepo;
        this.customer=(Customer) customer;
    }

    public void start(){
        while(true){
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

            switch (choice){

                case 1:{
                    addItemsToCart();
                    break;
                }

                case 2:{
                    removeItemsFromCart();
                    break;
                }

                case 3:{
                    viewCartSummary();
                    break;
                }

                case 4:{
                    placeOrder();
                    break;
                }

                case 5:{
                    viewOrderHistory();
                    break;
                }

                case 6:{
                    System.out.println("Logging out...");
                    System.out.println("================================================\n");
                    return;
                }

                default:{
                    System.out.println("Invalid choice! Please enter a number between 1 and 6.");
                }
            }
        }
    }

    private void viewOrderHistory() {
        List<Order> orderHistory=customer.getOrderHistory();
        if(orderHistory.isEmpty()){
            System.out.println("No orders placed yet.");
            return;
        }
        System.out.println(displayOrderHistoryHeader());
        for(Order o : orderHistory){
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
        try{
            customer.getCart().displayCart();
        }
        catch(EmptyCartException e){
            System.out.println("Exception: "+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }

    private void removeItemsFromCart() {
        Map<FoodItem, OrderItem> currentCart = customer.getCart().getShoppingCart();

        try{
            customer.getCart().displayCart();
        }
        catch(EmptyCartException e){
            System.out.println("Exception: "+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
            return;
        }

        System.out.print("Enter the FoodItem ID to remove: ");
        int id = validateInt();

        FoodItem itemToModify = null;

        for (FoodItem food : currentCart.keySet()) {
            if (food.getId() == id) {
                itemToModify = food;
                break;
            }
        }

        if (itemToModify != null) {

            OrderItem orderItem = currentCart.get(itemToModify);
            int currentQty = orderItem.getQuantity();

            System.out.println("Current quantity in cart: " + currentQty);
            System.out.print("Enter quantity to remove: ");
            int qtyToRemove = validateInt();

            if (qtyToRemove <= 0) {
                System.out.println("Invalid quantity!");
            }
            else if (qtyToRemove < currentQty) {
                orderItem.setQuantity(currentQty - qtyToRemove);
                System.out.println("Quantity updated successfully.");
            }
            else if (qtyToRemove == currentQty) {
                currentCart.remove(itemToModify);
                System.out.println("Item removed completely from cart.");
            }
            else {
                System.out.println("You cannot remove more than existing quantity!");
            }

        } else {
            System.out.println("Item not found in cart.");
        }

        while (true) {
            System.out.print("Do you want to remove more items? (y/n): ");
            String s = validateString();

            if (s.equalsIgnoreCase("y")) {
                removeItemsFromCart();
                break;
            }
            else if (s.equalsIgnoreCase("n")) {
                break;
            }
            else {
                System.out.println("Invalid input !!");
            }
        }
    }

    private void addItemsToCart() {
        Display.displayMenu(menuRepo.getMenu());
        System.out.print("Enter the FoodItem ID to add: ");
        int id=validateInt();
        FoodItem foodItem=menuRepo.getFoodItemById(id);
        if(foodItem==null){
            System.out.println("Invalid ID...");
            return;
        }
        System.out.print("Enter quantity: ");
        int quantity=validateInt();

        OrderItem orderItem=new OrderItem(foodItem,quantity);
        customer.getCart().addItem(orderItem);

        while(true){
            System.out.print("Do you want to add more items?(y/n) :");
            String s=validateString();
            if(s.equalsIgnoreCase("y")){
                addItemsToCart();
                break;
            }
            else if(s.equalsIgnoreCase("n")){
                break;
            }
            else{
                System.out.println("Invalid input !!");
            }
        }
    }


}
