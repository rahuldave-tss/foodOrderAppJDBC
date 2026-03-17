package com.tss.app;

import com.tss.controller.*;
import com.tss.entity.Customer;
import com.tss.entity.DeliveryPartner;
import com.tss.entity.User;
import com.tss.enums.Role;
import com.tss.exceptions.InvalidCredentialsException;
import com.tss.factory.UserFactory;
import com.tss.repository.impl.*;
import com.tss.service.*;

import static com.tss.utils.GlobalConstants.scanner;
import static com.tss.utils.Validate.*;

public class FoodOrderApp {

    private UserRepo userRepo;
    private DPRepo dpRepo;
    private MenuRepo menuRepo;
    private CustomerRepo customerRepo;
    private DiscountRepo discountRepo;
    private CartRepo cartRepo;
    private CartItemRepo cartItemRepo;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;
    private PaymentRepo paymentRepo;
    private DiscountService discountService;
    private DeliveryService deliveryService;

    public FoodOrderApp() {
        this.userRepo = new UserRepo();
        this.dpRepo = new DPRepo();
        this.menuRepo = new MenuRepo();
        this.customerRepo = new CustomerRepo();
        this.discountRepo = new DiscountRepo();
        this.cartRepo = new CartRepo();
        this.cartItemRepo = new CartItemRepo();
        this.orderRepo = new OrderRepo();
        this.orderItemRepo = new OrderItemRepo();
        this.paymentRepo = new PaymentRepo();

        this.deliveryService = new DeliveryService(dpRepo, orderRepo, orderItemRepo);
        this.discountService = new DiscountService(discountRepo);
    }

    public void start() {

        while (true) {
            System.out.println("\n================================================");
            System.out.println("            WELCOME TO FOOD ORDER APP        ");
            System.out.println("================================================");
            System.out.println(" Please choose an option:");
            System.out.println("------------------------------------------------");
            System.out.println("  1. Register");
            System.out.println("  2. Login");
            System.out.println("  3. Exit");
            System.out.println("------------------------------------------------");
            System.out.print(" Enter your choice (1-3): ");

            int choice = validateInt();
            System.out.println();

            switch (choice) {
                case 1:
                    register();
                    break;

                case 2:
                    try {
                        login();
                    } catch (InvalidCredentialsException e) {
                        System.out.println("Exception: " + e.getClass().getSimpleName());
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println(" Thank you for using Food Order App. Goodbye!");
                    System.out.println("================================================\n");
                    return;

                default:
                    System.out.println(" Invalid choice! Please enter a number between 1 and 3.");
            }
        }
    }

    private void login() {

        System.out.println("\n================================================");
        System.out.println("                     LOGIN                      ");
        System.out.println("================================================");

        System.out.print("Enter UserName: ");
        String userName = scanner.nextLine();

        User user = userRepo.getUserByUsername(userName);

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (user == null) {
            throw new InvalidCredentialsException();
        }

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException();
        }

        System.out.println("\nLogin Successful!");
        System.out.println("------------------------------------------------");

        redirectUser(user);

    }

    private void redirectUser(User user) {

        switch (user.getRole()) {
            case ADMIN: {
                AdminController adminController = new AdminController(
                        new AdminService(menuRepo, dpRepo, userRepo, discountRepo, customerRepo, orderRepo, deliveryService));
                System.out.println("Welcome Admin, " + user.getName() + "!");
                adminController.start();
                break;
            }
            case CUSTOMER: {
                Customer customer=customerRepo.getCustomerById(user.getId());
                CustomerController customerController = new CustomerController(
                        new CustomerService(customer, discountService, deliveryService,
                                orderRepo, orderItemRepo, paymentRepo, cartRepo, cartItemRepo),
                        menuRepo);
                System.out.println("Welcome Customer, " + user.getName() + "!");
                customerController.start();
                break;
            }
            case DELIVERY_PARTNER: {
                DeliveryPartner deliveryPartner=dpRepo.getDeliveryPartnerById(user.getId());
                DeliveryPartnerController deliveryPartnerController = new DeliveryPartnerController(
                        new DeliveryPartnerService(dpRepo, deliveryPartner, deliveryService, orderRepo, orderItemRepo), user);
                System.out.println("Welcome Delivery Partner, " + user.getName() + "!");
                deliveryPartnerController.start();
                break;
            }
        }
    }


    private void register() {

        while (true) {

            System.out.println("\n================================================");
            System.out.println("                   REGISTRATION                 ");
            System.out.println("================================================");
            System.out.println(" Please choose an option:");
            System.out.println("------------------------------------------------");
            System.out.println("  1. Customer");
            System.out.println("  2. Back");
            System.out.println("------------------------------------------------");
            System.out.print(" Enter your choice (1-2): ");

            int choice = validateInt();
            System.out.println();

            switch (choice) {
                case 1:
                    registerCustomer();
                    break;

                case 2:
                    return;

                default:
                    System.out.println(" Invalid choice! Please enter 1 or 2.");
            }
        }
    }

    private void registerCustomer() {

        System.out.println("\n------------------------------------------------");
        System.out.println("              CUSTOMER REGISTRATION             ");
        System.out.println("------------------------------------------------");

        String customerName = inputName();
        String customerUserName = inputUserName();
        String customerPassword = inputPassword();
        String customerEmail = inputEmail();
        String customerPhoneNumber = inputPhoneNumber();

        Customer customer =
                (Customer) UserFactory.createUser(Role.CUSTOMER, customerUserName, customerName, customerPassword, customerEmail, customerPhoneNumber);

        System.out.print("Enter your address: ");
        String address = scanner.nextLine();
        customer.setCustomerAddress(address);
        int customerId = userRepo.addUser(customer);
        customer.setId(customerId);
        customerRepo.addCustomer(customer, customerId);

        // Create a cart for the new customer
        cartRepo.createCart(customerId);

        System.out.println("\n New Customer Registered Successfully!");
        System.out.println(" Your Customer ID is: " + customerId);
        System.out.println("------------------------------------------------\n");
    }

    private String inputPhoneNumber() {
        String phoneNumber = validatePhoneNumber();
        while (!userRepo.canAddPhoneNumber(phoneNumber)) {
            System.out.println("Phone Number already registered !!");
            phoneNumber = validatePhoneNumber();
        }
        return phoneNumber;
    }

    private String inputEmail() {
        String email = validateEmail();
        while (!userRepo.canAddEmail(email)) {
            System.out.println("Email already registered !!");
            email = validateEmail();
        }
        return email;
    }

    private String inputUserName() {
        System.out.print("Enter userName: ");
        String userName = scanner.nextLine();
        while (!userRepo.canAddUsername(userName)) {
            System.out.println("UserName is Taken, Enter another one !!");
            userName = scanner.nextLine();
        }
        return userName;
    }


    private String inputPassword() {
        System.out.print("Enter your password: ");
        return scanner.nextLine();
    }

    private String inputName() {
        System.out.print("Enter your name: ");
        return validateString();
    }
}