package com.tss.entity;

import com.tss.enums.Role;

public class Admin extends User{

    public Admin(int id,String userName,String name, String password, String email, String phoneNumber) {
        super(id,userName,name, password, email, phoneNumber, Role.ADMIN);
    }
    public Admin(String userName,String name, String password, String email, String phoneNumber) {
        super(userName,name, password, email, phoneNumber, Role.ADMIN);
    }

}
