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
    String sql = "INSERT INTO customer (name, phone, email)";

    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, customer.getName());
      pstmt.setString(2, customer.getPhone());
      pstmt.setString(1, customer.getEmail());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Customer> getAll() {
    List<Customer> customers = new ArrayList<>();
    String sql = "SELECT * FROM customers";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        rs.getInt("id");
        rs.getString("name");
        rs.getString("phone");
        rs.getString("email");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return customers;
  }
}
