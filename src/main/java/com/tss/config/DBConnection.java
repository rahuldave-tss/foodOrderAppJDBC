package com.tss.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static Connection connection;

    private DBConnection(){}

    public static Connection connect(){
        try{
            if(connection==null){
                //Get properties from db.properties
                Properties props=new Properties();

                InputStream input= DBConnection.class
                        .getClassLoader()
                        .getResourceAsStream("db.properties");

                props.load(input);

                //1. Register Driver
                Class.forName(props.getProperty("db.driver"));
                //2. Establish Connection
                connection= DriverManager.getConnection(props.getProperty("db.url"),props.getProperty("db.username"),props.getProperty("db.password"));
                System.out.println("Connection Established with DB!!");

            }

        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        return connection;
    }
}

