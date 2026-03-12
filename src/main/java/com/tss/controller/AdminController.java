package com.tss.controller;

import com.tss.entity.*;
import com.tss.enums.Role;
import com.tss.factory.UserFactory;
import com.tss.service.AdminService;
import com.tss.exceptions.*;

import java.util.List;

import static com.tss.utils.GlobalConstants.*;
import static com.tss.utils.Validate.*;

public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public void start() {

        while (true) {
            System.out.println("\n==================================================");
            System.out.println("                  ADMIN PANEL                     ");
            System.out.println("==================================================");
            System.out.println("1. Manage Menu");
            System.out.println("2. Manage Discounts");
            System.out.println("3. Manage Delivery Partners");
            System.out.println("4. View All Customers");
            System.out.println("5. View All Delivery Partners");
            System.out.println("6. Logout");
            System.out.println("==================================================");
            System.out.print("Enter choice: ");

            int choice = validateInt();

            switch (choice) {
                case 1 :{
                    manageMenu();
                    break;
                }
                case 2 :{
                    manageDiscounts();
                    break;
                }
                case 3 :{
                    manageDeliveryPartners();
                    break;
                }
                case 4 :{
                    viewAllCustomers();
                    break;
                }
                case 5 :{
                    viewAllDeliveryPartners();
                    break;
                }
                case 6 : {
                    System.out.println("\nLogging out...");
                    return;
                }
                default : System.out.println("Invalid choice !!");
            }
        }
    }

    private void viewAllDeliveryPartners() {
        List<DeliveryPartner> deliveryPartnerList = adminService.getAllDeliveryPartners();
        if(deliveryPartnerList.isEmpty()){
            System.out.println("\nNo delivery partners available !!\n");
            return;
        }
        System.out.println("\n-------------- DELIVERY PARTNERS ----------------");
        System.out.println(deliveryPartnerHeader());
        deliveryPartnerList.forEach(System.out::println);
        System.out.println("-------------------------------------------------\n");
    }

    private String deliveryPartnerHeader() {
        return "+----+----------------+----------------+----------------+----------------------+--------------+-------------------+------------+\n" +
                "| ID | UserName       | Name           | Password       | Email                | Phone Number | Role              | Available  |\n" +
                "+----+----------------+----------------+----------------+----------------------+--------------+-------------------+------------+";
    }

    private void viewAllCustomers() {
        List<Customer> customerList = adminService.getAllCustomers();
        if(customerList.isEmpty()){
            System.out.println("\nNo customers available !!\n");
            return;
        }
        System.out.println("\n------------------- CUSTOMERS -------------------");
        System.out.println(customerHeader());
        customerList.forEach(System.out::println);
        System.out.println("-------------------------------------------------\n");
    }

    public static String customerHeader() {
        return "+----+----------------+----------------+----------------+----------------------+--------------+----------+----------------------+\n" +
                "| ID | UserName       | Name           | Password       | Email                | Phone Number | Role     | Address              |\n" +
                "+----+----------------+----------------+----------------+----------------------+--------------+----------+----------------------+";
    }

    private void manageMenu() {

        System.out.println("\n================= MANAGE MENU =================");
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. View Items");
        System.out.println("===============================================");
        System.out.print("Enter choice: ");

        int choice = validateInt();

        switch (choice) {
            case 1: {
                viewAllItems();
                System.out.println("\n----------- ADD NEW ITEM -----------");
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                try{
                    if(adminService.itemAlreadyPresent(name)){
                        throw new ItemAlreadyPresentException("Item already present in the menu !!");
                    }
                }
                catch (ItemAlreadyPresentException e){
                    System.out.println("Exception: " + e.getClass().getSimpleName());
                    System.out.println(e.getMessage());
                    break;
                }

                System.out.print("Enter price: ");
                double price = validateDouble();
                int id = adminService.addItem(name, price);
                System.out.println("\nItem added successfully with ID: " + id);

                break;
            }

            case 2: {
                viewAllItems();
                System.out.print("\nEnter item ID to remove: ");
                int id = validateInt();
                try {
                    boolean removed = adminService.removeItem(id);
                    System.out.println(removed ? "\nItem removed successfully." : "\nItem not found.");
                } catch (EmptyMenuException e) {
                    System.out.println("Exception: " + e.getClass().getSimpleName());
                    System.out.println(e.getMessage());
                }
                break;
            }

            case 3: {
                viewAllItems();
                break;
            }

            default:
                System.out.println("Invalid choice !!");
        }
    }

    private void viewAllItems() {
        List<FoodItem> items = adminService.getAllItems();
        if(items.isEmpty()){
            System.out.println("\nNo items available in the menu !!\n");
            return;
        }
        System.out.println("\n------------------- MENU ITEMS -------------------");
        items.forEach(i ->
                System.out.printf("ID: %-5d Name: %-20s Price: %.2f%n",
                        i.getId(), i.getName(), i.getPrice()));
        System.out.println("--------------------------------------------------\n");
    }

    private void manageDiscounts() {

        System.out.println("\n=============== MANAGE DISCOUNTS ===============");
        System.out.println("1. View Discounts");
        System.out.println("2. Add Discount");
        System.out.println("3. Remove Discount");
        System.out.println("4. Update Discount");
        System.out.println("================================================");
        System.out.print("Enter choice: ");

        int choice = validateInt();

        switch (choice) {
            case 1: {
                viewAllDiscounts();
                break;
            }

            case 2: {
                System.out.println("\n----------- ADD DISCOUNT -----------");
                System.out.print("Enter amount: ");
                double amount = validateDouble();
                if(adminService.findDiscountByAmount(amount)){
                    System.out.println("\nDiscount already present for this amount !!");
                    break;
                }

                double percentage = validatePercentage();

                DiscountStrategy discount =
                        new Discount("Amount Discount", amount, percentage);
                adminService.addDiscount(discount);

                System.out.println("\nDiscount added successfully !!");
                break;
            }

            case 3: {
                viewAllDiscounts();
                System.out.print("Enter Discount ID to remove: ");
                int discountId = validateInt();

                if (!adminService.removeDiscount(discountId)) {
                    System.out.println("\nDiscount not available !!");
                    break;
                }
                System.out.println("\nDiscount removed successfully !!");
                break;
            }

            case 4: {
                viewAllDiscounts();
                System.out.print("Enter Discount ID to update: ");
                int discountId = validateInt();

                adminService.updateDiscount(discountId);
                break;
            }

            default:
                System.out.println("Enter a valid choice !!");
        }
    }

    private void viewAllDiscounts() {
        List<DiscountStrategy> discounts = adminService.getAllDiscounts();
        if(discounts.isEmpty()){
            System.out.println("\nNo discounts available !!\n");
            return;
        }
        System.out.println("\n---------------- DISCOUNT LIST ----------------");
        discounts.forEach(d ->
                        System.out.printf("ID: %-5d Amount: %-10.2f Percentage: %.2f%%%n",
                                d.getDiscountId(),
                                d.getDiscountAmount(),
                                d.getDiscountPercentage()));
        System.out.println("------------------------------------------------\n");
    }

    private void manageDeliveryPartners() {

        System.out.println("\n========== MANAGE DELIVERY PARTNERS ==========");
        System.out.println("1. Add Partner");
        System.out.println("2. Remove Partner");
        System.out.println("3. View Partners");
        System.out.println("==============================================");
        System.out.print("Enter choice: ");

        int choice = validateInt();

        switch (choice) {
            case 1: {
                System.out.println("\n----------- ADD DELIVERY PARTNER -----------");

                System.out.print("Enter name: ");
                String name = validateString();

                System.out.print("Enter userName: ");
                String userName= scanner.nextLine();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                String email = validateEmail();
                String phoneNumber = validatePhoneNumber();

                User partner = UserFactory.createUser(
                        Role.DELIVERY_PARTNER,
                        userName,
                        name,
                        password,
                        email,
                        phoneNumber
                );

                adminService.addDeliveryPartner(partner);

                break;
            }

            case 2: {
                viewAllDeliveryPartners();
                if(adminService.getAllDeliveryPartners().isEmpty()){
                    break;
                }
                System.out.print("Enter Delivery Partner ID to remove: ");
                int partnerId = validateInt();
                adminService.removeDeliveryPartner(partnerId);
                break;
            }

            case 3: {
                viewAllDeliveryPartners();
                break;
            }
        }
    }
}