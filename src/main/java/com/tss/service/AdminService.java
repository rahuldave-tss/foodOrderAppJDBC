package com.tss.service;

import com.tss.exceptions.EmptyMenuException;
import com.tss.entity.*;
import com.tss.repository.*;

import java.util.List;

import static com.tss.utils.Validate.validatePercentage;

public class AdminService {

    private MenuRepo menuRepo;
    private DiscountRepo discountRepo;
    private DPRepo dpRepo;
    private UserRepo userRepo;
    private CustomerRepo customerRepo;
    private DeliveryManager deliveryManager;

    public AdminService(MenuRepo menuRepo,
                        DPRepo dpRepo,
                        UserRepo userRepo,
                        DiscountRepo discountRepo, CustomerRepo customerRepo,DeliveryManager deliveryManager) {
        this.menuRepo = menuRepo;
        this.dpRepo = dpRepo;
        this.userRepo = userRepo;
        this.discountRepo = discountRepo;
        this.customerRepo=customerRepo;
        this.deliveryManager=deliveryManager;
    }

    public int addItem(String itemName, double price) {
        FoodItem foodItem=new FoodItem(itemName,price);
        menuRepo.addItem(foodItem);
        return foodItem.getId();
    }

    public boolean removeItem(int itemId) {
        if (menuRepo.getMenu().isEmpty()) {
            throw new EmptyMenuException("No food items available");
        }
        FoodItem foodItem=menuRepo.getFoodItemById(itemId);
        if(foodItem==null){
            return false;
        }
        menuRepo.removeItem(foodItem);
        return true;
    }

    public List<FoodItem> getAllItems() {
        return menuRepo.getMenu();
    }

    public boolean itemAlreadyPresent(String itemName) {
        return menuRepo.getMenu()
                .stream()
                .anyMatch(f -> f.getName().equalsIgnoreCase(itemName));
    }

    public void addDiscount(DiscountStrategy discount) {
        discountRepo.getAvailableDiscounts().add(discount);
    }

    public boolean removeDiscount(int discountId) {
        return discountRepo.getAvailableDiscounts()
                .removeIf(d -> d.getDiscountId() == discountId);
    }

    public void updateDiscount(int id) {
        DiscountStrategy discount= discountRepo.findById(id);
        if(discount==null){
            System.out.println("No such discount available !!");
            return;
        }
        double newPercentage=validatePercentage();
        discount.setDiscountPercentage(newPercentage);
        System.out.println("\nDiscount updated successfully !!");
    }

    public boolean findDiscountByAmount(double amount){
        return discountRepo.findDiscountByAmount(amount);
    }

    public List<DiscountStrategy> getAllDiscounts() {
        return discountRepo.getAvailableDiscounts();
    }

    public void addDeliveryPartner(User partner) {
        userRepo.addUser(partner);
        dpRepo.addPartner((DeliveryPartner) partner);
        System.out.println("\nPartner added successfully with ID: " + partner.getId());
        if(!deliveryManager.getPendingOrders().isEmpty()){
            deliveryManager.assignNextPendingOrder((DeliveryPartner) partner);
        }
    }

    public void removeDeliveryPartner(int partnerId) {
        DeliveryPartner partner = dpRepo.getDeliveryPartnerById(partnerId);
        if (partner == null){
            System.out.println("No such partner found !!");
            return;
        }
        //if removed, all his active orders will be set to pending and added back to pending queue
        List<Order> orders = dpRepo.getDeliveryPartnerOrders(partner);
        for(Order o : orders){
            o.setDeliveryPartner(null);
            o.setStatus(OrderStatus.PENDING);
            deliveryManager.getPendingOrders().add(o);
        }

        userRepo.removeUser(partner.getUserName());
        dpRepo.removePartner(partner);
        System.out.println("Delivery Partner removed successfully !!");
    }

    public List<DeliveryPartner> getAllDeliveryPartners() {
        return dpRepo.getDeliveryPartners();
    }

    public List<Customer> getAllCustomers(){
        return customerRepo.getCustomerList();
    }
}