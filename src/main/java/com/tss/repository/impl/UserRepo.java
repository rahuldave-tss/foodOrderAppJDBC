package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.*;
import com.tss.enums.Role;
import com.tss.repository.IUserRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepo implements IUserRepo {
    private Connection connection;

    public UserRepo(){
        this.connection=DBConnection.connect();
    }

    public int addUser(User user){
        int userId=-1;
        String sql="Insert into users(name,user_name,password,phone_number,email,role)" +
                    "VALUES (?,?,?,?,?,?::user_role) Returning id";

        try{
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getUserName());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setString(4,user.getPhoneNumber());
            preparedStatement.setString(5,user.getEmail());
            preparedStatement.setString(6,user.getRole().name()); //enum->string

            ResultSet resultSet=preparedStatement.executeQuery();
//            System.out.println("if ni baar");
            if(resultSet.next()){
//                System.out.println("if ni andar");
                userId=resultSet.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userId;
    }

    public void removeUserByUsername(String userName){
        String sql="Delete from users where user_name=?";

        try {
            PreparedStatement preparedStatement= connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    public User getUserByUsername(String userName){
        String sql="SELECT * from users where user_name=?";

        try {
            PreparedStatement preparedStatement= connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            ResultSet rs=preparedStatement.executeQuery();

            if(rs.next()){
                Role role= Role.valueOf(rs.getString("role"));

                if(role.equals(Role.ADMIN)){
                    return new Admin(rs.getInt("id"),
                            rs.getString("user_name"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("phone_number"),
                            rs.getString("email"));
                }
                else if(role.equals(Role.CUSTOMER)){
                    return new Customer(rs.getInt("id"),
                            rs.getString("user_name"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("phone_number"),
                            rs.getString("email"));
                }
                else if(role.equals(Role.DELIVERY_PARTNER)){
                    return new DeliveryPartner(rs.getInt("id"),
                            rs.getString("user_name"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("phone_number"),
                            rs.getString("email"));
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public boolean canAddPhoneNumber(String phoneNumber) {
        String sql="Select * from users where phone_number=?";

        try{
            PreparedStatement ps= connection.prepareStatement(sql);
            ps.setString(1,phoneNumber);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public boolean canAddEmail(String email){
        String sql="Select * from users where email=?";

        try{
            PreparedStatement ps= connection.prepareStatement(sql);
            ps.setString(1,email);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean canAddUsername(String userName) {
        String sql="Select * from users where user_name=?";

        try{
            PreparedStatement ps= connection.prepareStatement(sql);
            ps.setString(1,userName);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
