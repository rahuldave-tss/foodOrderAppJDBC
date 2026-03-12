package com.tss.factory;

import com.tss.entity.*;

public class UserFactory {

    private UserFactory() {} // prevent instantiation

    public static User createUser(Role role,
                                  String userName,
                                  String name,
                                  String password,
                                  String email,
                                  String phoneNumber) {

        switch (role) {
            case ADMIN:
                return new Admin(userName,name, password, email, phoneNumber);

            case CUSTOMER:
                return new Customer(userName,name, password, email, phoneNumber);

            case DELIVERY_PARTNER:
                return new DeliveryPartner(userName,name, password, email, phoneNumber);

            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}