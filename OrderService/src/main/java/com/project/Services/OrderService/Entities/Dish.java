package com.project.Services.OrderService.Entities;

import java.math.BigDecimal;

public class Dish {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
