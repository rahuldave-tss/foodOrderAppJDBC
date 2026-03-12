package com.tss.repository;

import com.tss.entity.Admin;
import com.tss.entity.User;

import java.util.HashMap;
import java.util.Map;

import static com.tss.utils.GlobalConstants.*;

public class UserRepo {
    //userName -> user
    Map<String, User> userList;

    public UserRepo() {
        this.userList = new HashMap<>();
        init();
    }

    public void addUser(User user){
        userList.put(user.getUserName(),user);
    }
    public void removeUser(String userName){
        userList.remove(userName);
    }

    private void init(){
        //Hardcoding an admin user for testing
        Admin admin=new Admin(adminUsername,adminName,adminPassword, adminEmail,adminPhoneNumber);
        userList.put(adminUsername, admin);
    }

    public User getUserById(int id){
        return userList.getOrDefault(id,null);
    }

    public User getUserByUserName(String userName){
        return userList.getOrDefault(userName,null);
    }
}
