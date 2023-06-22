package com.project.Services.OrderService.Entities;

import java.sql.Timestamp;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private String status;
    private String specialRequests;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<OrderDish> dishes;

    public int getUserId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public List<OrderDish> getDishes() {
        return dishes;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;

    }

    public void setDishes(List<OrderDish> orderDishes) {
        this.dishes = orderDishes;
    }

}
