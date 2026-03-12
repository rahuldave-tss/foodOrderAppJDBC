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
                FoodItem f=new FoodItem(resultSet.getString("name"),resultSet.getDouble("price"));
                menu.add(f);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return menu;
    }

    public void addItem(FoodItem foodItem){
        String sql="Insert into food_item values(name,price) values (?,?)";

        try{
            PreparedStatement preparedStatement= connection.prepareStatement(sql);
            preparedStatement.setString(1, foodItem.getName());
            preparedStatement.setDouble(2,foodItem.getPrice());

            preparedStatement.executeUpdate();

        }
        catch(SQLException e){
            System.out.println("Exception: "+e.getMessage());
        }
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
            foodItem=new FoodItem(resultSet.getString("name"),resultSet.getDouble("price"));

        }
        catch(SQLException e){
            System.out.println("Exception: "+e.getMessage());
        }

        return foodItem;
    }
}
