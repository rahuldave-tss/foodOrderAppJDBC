package com.tss.entity;

import com.tss.enums.userRole;

public class Admin extends User{

    public Admin(String name, String userName, String password, String email, String phoneNumber, userRole role) {
        super(name, userName, password, email, phoneNumber, role);
    }
}
