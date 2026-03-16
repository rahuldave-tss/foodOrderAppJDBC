package com.tss.service;

import com.tss.enums.OrderStatus;
import com.tss.exceptions.EmptyMenuException;
import com.tss.entity.*;
import com.tss.repository.impl.*;

import java.util.List;

import static com.tss.utils.Validate.validatePercentage;

public class AdminService {

    private MenuRepo menuRepo;
    private DiscountRepo discountRepo;
    private DPRepo dpRepo;
    private UserRepo userRepo;
    private CustomerRepo customerRepo;
    private OrderRepo orderRepo;
    private DeliveryService deliveryService;

    public AdminService(MenuRepo menuRepo,
                        DPRepo dpRepo,
                        UserRepo userRepo,
                        DiscountRepo discountRepo,
                        CustomerRepo customerRepo,
                        OrderRepo orderRepo,
                        DeliveryService deliveryService) {
        this.menuRepo = menuRepo;
        this.dpRepo = dpRepo;
        this.userRepo = userRepo;
        this.discountRepo = discountRepo;
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.deliveryService = deliveryService;
    }

    public int addItem(String itemName, double price) {
        return menuRepo.addItem(new FoodItem(itemName, price));
    }

    public boolean removeItem(int itemId) {
        if (menuRepo.getMenu().isEmpty()) {
            throw new EmptyMenuException("No food items available");
        }
        FoodItem foodItem = menuRepo.getFoodItemById(itemId);
        if (foodItem == null) {
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

    public int addDiscount(DiscountStrategy discount) {
        return discountRepo.addDiscount(discount);
    }

    public boolean removeDiscount(int discountId) {
        return discountRepo.removeDiscount(discountId);
    }

    public void updateDiscount(int id) {
        DiscountStrategy discount = discountRepo.findById(id);
        if (discount == null) {
            System.out.println("No such discount available !!");
            return;
        }
        double newPercentage = validatePercentage();
        discountRepo.updateDiscountPercentage(id, newPercentage);
        System.out.println("\nDiscount updated successfully !!");
    }

    public boolean findDiscountByAmount(double amount) {
        return discountRepo.findDiscountByAmount(amount);
    }

    public List<DiscountStrategy> getAllDiscounts() {
        return discountRepo.getAvailableDiscounts();
    }

    public void addDeliveryPartner(User partner) {
        int userId = userRepo.addUser(partner);
        if(userId==-1){
            return;
        }
        partner.setId(userId);
        dpRepo.addPartner((DeliveryPartner) partner);
        System.out.println("\nPartner added successfully with ID: " + partner.getId());

        List<Order> pendingOrders = orderRepo.getPendingOrders();
        if (!pendingOrders.isEmpty()) {
            deliveryService.assignNextPendingOrder((DeliveryPartner) partner);
        }
    }

    public void removeDeliveryPartner(int partnerId) {
        DeliveryPartner partner = dpRepo.getDeliveryPartnerById(partnerId);
        if (partner == null) {
            System.out.println("No such partner found !!");
            return;
        }

        List<Order> orders = orderRepo.getOrdersByDeliveryPartnerId(partnerId);
        for (Order o : orders) {
            if (o.getStatus() != OrderStatus.DELIVERED) {
                orderRepo.updateOrderStatus(o.getOrderId(), OrderStatus.PENDING);
                orderRepo.assignDeliveryPartner(o.getOrderId(), 0); // unassign
            }
        }

        dpRepo.removePartner(partner);
        userRepo.removeUserByUsername(partner.getUserName());
        System.out.println("Delivery Partner removed successfully !!");
    }

    public List<DeliveryPartner> getAllDeliveryPartners() {
        return dpRepo.getDeliveryPartners();
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.getCustomerList();
    }
}