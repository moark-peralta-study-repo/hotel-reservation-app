package org.hotel.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.hotel.db.Database;
import org.hotel.model.Room;

public class RoomDAO {
  public void insert(Room room) {
    String sql = "INSERT INTO rooms (room_number, type, price, is_available) VALUES (?, ?, ?, ?)";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, room.getRoomNumber());
      pstmt.setString(2, room.getType());
      pstmt.setDouble(3, room.getPrice());
      pstmt.setInt(4, room.isAvailable() ? 1 : 0);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Room> getAll() {
    List<Room> rooms = new ArrayList<>();
    String sql = "SELECT * FROM rooms";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {

        rooms.add(new Room(
            rs.getInt("id"),
            rs.getInt("room_number"),
            rs.getString("type"),
            rs.getDouble("price"),
            rs.getInt("is_available") == 1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rooms;
  }
}
