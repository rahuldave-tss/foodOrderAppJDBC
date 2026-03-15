package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.Cart;
import com.tss.entity.Customer;
import com.tss.repository.ICartRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartRepo implements ICartRepo {
    private Connection connection;

    public CartRepo() {
        connection= DBConnection.connect();
    }

    @Override
    public Cart createCart(int customerId) {
        String sql="Insert into cart(customer_id) values (?) Returning id";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,customerId);

            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                int cartId=resultSet.getInt("id");
                Cart cart=new Cart(cartId,customerId);
                return cart;
            }

        } catch (SQLException e) {
            System.out.println("Exception: "+e.getMessage());
        }
        return null;
    }

    @Override
    public Cart getCartByUserId(int userId) {
        String sql = "SELECT * FROM cart WHERE customer_id=?";

        try{
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,userId);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                int id = rs.getInt("id");
                return new Cart(id,userId);
            }

        }catch(SQLException e){
            System.out.println("Exception: "+e.getMessage());
        }

        return null;
    }
}
