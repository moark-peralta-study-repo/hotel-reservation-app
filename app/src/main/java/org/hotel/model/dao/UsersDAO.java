package org.hotel.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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
            rs.getString("password").toCharArray(),
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
              rs.getString("password").toCharArray(),
              UserRole.valueOf(rs.getString("role")));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();

    }
    return null;
  }

  public User login(String username, char[] password) {
    User user = findByUsername(username);
    if (user == null)
      return null;

    boolean ok = Arrays.equals(user.getPassword(), password);

    Arrays.fill(password, '\0');

    return ok ? user : null;
  }

  public boolean usernameExists(String username) {
    String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {

      e.printStackTrace();
      return true;
    }
  }

  public void insert(User user) {
    String sql = """
          INSERT INTO users (first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?)
        """;

    char[] pw = user.getPassword();
    String pwStr = new String(pw);

    Arrays.fill(pw, '\0');

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, user.getFirstName());
      stmt.setString(2, user.getLastName());
      stmt.setString(3, user.getUsername());
      stmt.setString(4, pwStr);
      stmt.setString(5, user.getRole().name());

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void update(User user) {
    String sql = "UPDATE users SET first_name=?, last_name=?, username=?, password=?, role=? WHERE id=?";

    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      char[] pw = user.getPassword();
      String pwStr = new String(pw);

      stmt.setString(1, user.getFirstName());
      stmt.setString(2, user.getLastName());
      stmt.setString(3, user.getUsername());
      stmt.setString(4, pwStr);
      stmt.setString(5, user.getRole().name());
      stmt.setInt(6, user.getId());

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void delete(int id) {
    String sql = "DELETE FROM users WHERE id=?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);
      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public User getById(int id) {
    String sql = "SELECT * FROM users WHERE id=?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return new User(
              rs.getInt("id"),
              rs.getString("first_name"),
              rs.getString("last_name"),
              rs.getString("username"),
              rs.getString("password").toCharArray(),
              UserRole.valueOf(rs.getString("role").toUpperCase()));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}
