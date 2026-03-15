package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.FoodItem;
import com.tss.repository.IMenuRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepo implements IMenuRepo {
    private Connection connection;

    public MenuRepo() {
        connection= DBConnection.connect();
    }

    public List<FoodItem> getMenu() {
        List<FoodItem> menu=new ArrayList<>();
        String sql="Select * from food_item";

        try{
            Statement statement= connection.createStatement();
            ResultSet resultSet= statement.executeQuery(sql);

            while(resultSet.next()){
                FoodItem f=new FoodItem(resultSet.getInt("id"),resultSet.getString("name"),resultSet.getDouble("price"));
                menu.add(f);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return menu;
    }

    public int addItem(FoodItem foodItem){
        int itemId=-1;
        String sql="Insert into food_item (name,price) values (?,?) returning id";

        try{
            PreparedStatement preparedStatement= connection.prepareStatement(sql);
            preparedStatement.setString(1, foodItem.getName());
            preparedStatement.setDouble(2,foodItem.getPrice());

            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                itemId=resultSet.getInt("id");
            }

        }
        catch(SQLException e){
            System.out.println("Exception: "+e.getMessage());
        }
        return itemId;
    }
    public void removeItem(FoodItem foodItem){
        String sql="Delete from food_item where id=?";

        try{
            PreparedStatement preparedStatement= connection.prepareStatement(sql);
            preparedStatement.setInt(1, foodItem.getId());

            preparedStatement.executeUpdate();

        }
        catch(SQLException e){
            System.out.println("Exception: "+e.getMessage());
        }
    }

    public FoodItem getFoodItemById(int itemId){
        String sql="Select * from food_item where id=?";
        FoodItem foodItem = null;
        try{
            PreparedStatement preparedStatement= connection.prepareStatement(sql);
            preparedStatement.setInt(1, itemId);

            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                foodItem=new FoodItem(resultSet.getInt("id"),resultSet.getString("name"),resultSet.getDouble("price"));
            }

        }
        catch(SQLException e){
            System.out.println("Exception: "+e.getMessage());
        }

        return foodItem;
    }
}
