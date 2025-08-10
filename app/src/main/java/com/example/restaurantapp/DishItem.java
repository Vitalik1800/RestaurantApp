package com.example.restaurantapp;

import java.io.Serializable;

public class DishItem implements Serializable {

    private int dishId;
    private double price;
    private int stockQuantity;

    public DishItem(int dishId, double price, int stockQuantity) {
        this.dishId = dishId;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
