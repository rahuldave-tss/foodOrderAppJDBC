package com.tss.repository;

import com.tss.entity.User;

public interface IUserRepo {
    int addUser(User user);
    void removeUserByUsername(String userName);
    User getUserByUsername(String userName);
    boolean canAddPhoneNumber(String phoneNumber);
    boolean canAddEmail(String email);
    boolean canAddUsername(String userName);
}
