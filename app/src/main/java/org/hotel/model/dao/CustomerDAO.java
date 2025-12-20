package org.hotel.model.dao;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.hotel.db.Database;
import org.hotel.model.Customer;

public class CustomerDAO {

  public void insert(Customer customer) {
    String sql = "INSERT INTO customers (name, phone, email) VALUES (?, ?, ?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, customer.getName());
      pstmt.setString(2, customer.getPhone());
      pstmt.setString(3, customer.getEmail());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Customer> getAll() {
    List<Customer> customers = new ArrayList<>();
    String sql = "SELECT * FROM customers WHERE is_active = 1";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Customer customer = new Customer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getString("email"));

        customers.add(customer);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return customers;
  }

  public void deactivate(int customerId) {
    String sql = "UPDATE customers SET is_active = 0 WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, customerId);
      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Customer getById(int id) {
    String sql = "SELECT * FROM customers WHERE id = ? AND is_active = 1";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return new Customer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getString("email"));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}
