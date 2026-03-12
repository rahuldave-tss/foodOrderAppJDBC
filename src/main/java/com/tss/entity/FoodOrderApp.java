package com.tss.entity;

import com.tss.controller.*;
import com.tss.exceptions.InvalidCredentialsException;
import com.tss.factory.UserFactory;
import com.tss.repository.*;
import com.tss.service.*;

import java.util.LinkedList;

import static com.tss.utils.GlobalConstants.scanner;
import static com.tss.utils.Validate.*;

public class FoodOrderApp {

    private UserRepo userRepo;
    private DPRepo dpRepo;
    private MenuRepo menuRepo;
    private CustomerRepo customerRepo;
    private DiscountRepo discountRepo;
    private DiscountService discountService;
    private DeliveryManager deliveryManager;

    public FoodOrderApp() {
        this.dpRepo = new DPRepo();
        this.userRepo = new UserRepo();
        this.menuRepo = new MenuRepo();
        this.discountRepo=new DiscountRepo();
        this.customerRepo=new CustomerRepo();

        this.deliveryManager=new DeliveryManager(dpRepo,new LinkedList<>());

        this.discountService=new DiscountService(discountRepo);
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
                    try{
                        login();
                    }
                    catch (InvalidCredentialsException e){
                        System.out.println("Exception: "+e.getClass().getSimpleName());
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

        User user = userRepo.getUserByUserName(userName);

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

        switch (user.getRole()){
            case ADMIN:{
                AdminController adminController=new AdminController(new AdminService(menuRepo,dpRepo,userRepo,discountRepo,customerRepo,deliveryManager));
                System.out.println("Welcome Admin, "+user.getName()+"!");
                adminController.start();
                break;
            }
            case CUSTOMER:{
                CustomerController customerController=new CustomerController(new CustomerService(dpRepo,discountRepo,user,discountService,deliveryManager),menuRepo,user);
                System.out.println("Welcome Customer, "+user.getName()+"!");
                customerController.start();
                break;
            }
            case DELIVERY_PARTNER:{
                DeliveryPartnerController deliveryPartnerController=new DeliveryPartnerController(new DeliveryPartnerService(dpRepo,user,deliveryManager),user);
                System.out.println("Welcome Delivery Partner, "+user.getName()+"!");
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
        String customerUserName= inputUserName();
        String customerPassword = inputPassword();
        String customerEmail=validateEmail();
        String customerPhoneNumber=validatePhoneNumber();

        Customer customer =
                (Customer) UserFactory.createUser(Role.CUSTOMER,customerUserName,customerName,customerPassword,customerEmail,customerPhoneNumber);

        System.out.print("Enter your address: ");
        String address= scanner.nextLine();
        customer.setCustomerAddress(address);
        userRepo.addUser(customer);
        customerRepo.addCustomer(customer);

        System.out.println("\n New Customer Registered Successfully!");
        System.out.println(" Your Customer ID is: " + customer.getId());
        System.out.println("------------------------------------------------\n");
    }

    private String inputUserName() {
        System.out.print("Enter userName: ");
        return scanner.nextLine();
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