package com.project.Services.OrderService;

import com.project.Services.OrderService.Entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderController {
    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:authorization.db";

    private void connect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
    }

    private void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        try {
            connect();

            if (!isValidOrder(order)) {
                return ResponseEntity.badRequest().body("Incorrect order data");
            }

            if (!areDishesAvailable(order)) {
                return ResponseEntity.badRequest().body("There are unavailable dishes");
            }

            String query = "INSERT INTO orders (user_id, status, special_requests) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, order.getUserId());
            statement.setString(2, order.getStatus());
            statement.setString(3, order.getSpecialRequests());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 1) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);

                    query = "INSERT INTO order_dish (order_id, dish_id, quantity, price) VALUES (?, ?, ?, ?)";
                    statement = connection.prepareStatement(query);
                    for (OrderDish orderDish : order.getDishes()) {
                        statement.setInt(1, orderId);
                        statement.setInt(2, orderDish.getDishId());
                        statement.setInt(3, orderDish.getQuantity());
                        statement.setBigDecimal(4, orderDish.getPrice());
                        statement.executeUpdate();
                    }

                    disconnect();

                    return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
                }
            }

            disconnect();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order");
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable int orderId) {
        try {
            connect();

            String query = "SELECT * FROM orders WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getInt("id"));
                order.setUserId(resultSet.getInt("user_id"));
                order.setStatus(resultSet.getString("status"));
                order.setSpecialRequests(resultSet.getString("special_requests"));
                order.setCreatedAt(resultSet.getTimestamp("created_at"));
                order.setUpdatedAt(resultSet.getTimestamp("updated_at"));

                query = "SELECT * FROM order_dish WHERE order_id = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, orderId);
                ResultSet orderDishesResultSet = statement.executeQuery();

                List<OrderDish> orderDishes = new ArrayList<>();
                while (orderDishesResultSet.next()) {
                    OrderDish orderDish = new OrderDish();
                    orderDish.setId(orderDishesResultSet.getInt("id"));
                    orderDish.setOrderId(orderDishesResultSet.getInt("order_id"));
                    orderDish.setDishId(orderDishesResultSet.getInt("dish_id"));
                    orderDish.setQuantity(orderDishesResultSet.getInt("quantity"));
                    orderDish.setPrice(orderDishesResultSet.getBigDecimal("price"));
                    orderDishes.add(orderDish);
                }

                order.setDishes(orderDishes);

                disconnect();

                return ResponseEntity.ok(order);
            } else {
                disconnect();
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/menu")
    public ResponseEntity<List<Dish>> getMenu() {
        try {
            connect();

            String query = "SELECT * FROM dish WHERE quantity > 0";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<Dish> menu = new ArrayList<>();
            while (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("id"));
                dish.setName(resultSet.getString("name"));
                dish.setDescription(resultSet.getString("description"));
                dish.setPrice(resultSet.getBigDecimal("price"));
                dish.setQuantity(resultSet.getInt("quantity"));
                menu.add(dish);
            }

            disconnect();

            return ResponseEntity.ok(menu);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidOrder(Order order) {
        return order.getUserId() != 0 && order.getDishes() != null && !order.getDishes().isEmpty();
    }

    private boolean areDishesAvailable(Order order) {
        try {
            String query = "SELECT COUNT(*) FROM dish WHERE id = ? AND quantity >= ?";
            PreparedStatement statement = connection.prepareStatement(query);

            for (OrderDish orderDish : order.getDishes()) {
                statement.setInt(1, orderDish.getDishId());
                statement.setInt(2, orderDish.getQuantity());
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next() || resultSet.getInt(1) == 0) {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

