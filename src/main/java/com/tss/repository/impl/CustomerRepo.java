package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.Customer;
import com.tss.entity.User;
import com.tss.repository.ICustomerRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepo implements ICustomerRepo {
    private Connection connection;

    public CustomerRepo() {
        connection= DBConnection.connect();
    }

    public void addCustomer(Customer customer,int customerId){
        String sql="Insert into customer(user_id,address) Values (?,?)";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,customerId);
            preparedStatement.setString(2,customer.getCustomerAddress());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    public void removeCustomer(Customer customer){
        String sql="Delete from customer where user_name=?";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,customer.getUserName());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    public List<Customer> getCustomerList() {
        List<Customer> customers=new ArrayList<>();

        String sql="Select c.*,u.* from customer c JOIN users u ON c.user_id=u.id";
        try{
            Statement statement= connection.createStatement();
            ResultSet resultSet=statement.executeQuery(sql);

            while(resultSet.next()){
                int id=resultSet.getInt("id");
                String name=resultSet.getString("name");
                String user_name=resultSet.getString("user_name");
                String password=resultSet.getString("password");
                String phone_number=resultSet.getString("phone_number");
                String email=resultSet.getString("email");
                String address=resultSet.getString("address");

                Customer c=new Customer(id,user_name,name,password,email,phone_number,address);
                customers.add(c);
            }


        }
        catch(SQLException e){
            System.out.println("Exception: "+e.getMessage());
        }

        return customers;
    }
}
