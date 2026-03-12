package com.tss;

import com.tss.config.DBConnection;
import com.tss.entity.FoodOrderApp;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        FoodOrderApp foodOrderApp=new FoodOrderApp();
        foodOrderApp.start();

//        Connection connection= DBConnection.connect();
//        System.out.println(connection);
    }
}
