package org.hotel.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hotel.db.Database;
import org.hotel.model.User;
import org.hotel.model.UserRole;

public class UsersDAO {

  public List<User> getAll() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        users.add(new User(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("username"),
            rs.getString("password"),
            UserRole.valueOf(rs.getString("role").toUpperCase())));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return users;
  }

  public User findByUsername(String username) {
    String sql = "SELECT * FROM users WHERE username = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);

      try (ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
          return new User(
              rs.getInt("id"),
              rs.getString("first_name"),
              rs.getString("last_name"),
              rs.getString("username"),
              rs.getString("password"),
              UserRole.valueOf(rs.getString("role")));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();

    }
    return null;
  }

  public User login(String username, String password) {
    User user = findByUsername(username);
    if (user == null)
      return null;

    return user.getPassword().equals(password) ? user : null;
  }
}
