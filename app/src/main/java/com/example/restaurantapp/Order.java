package com.example.restaurantapp;

import java.util.List;

public class Order {

    int orderId;
    String orderStatus, createdAt;
    double totalPrice;
    List<Dish> dishes;

    public Order(int orderId, String orderStatus, double totalPrice, String createdAt, List<Dish> dishes) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.dishes = dishes;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<Dish> getDishes() {
        return dishes;
    }
}
