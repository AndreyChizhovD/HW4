package com.project.Services.OrderService.Entities;

import java.math.BigDecimal;

public class OrderDish {
    private int id;
    private int orderId;
    private int dishId;
    private int quantity;
    private BigDecimal price;

    public int getDishId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
