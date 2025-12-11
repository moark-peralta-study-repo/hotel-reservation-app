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

  public Room getByRoomNumber(int roomNumber) {
    String sql = "SELECT * FROM rooms WHERE room_number = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, roomNumber);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return new Room(
            rs.getInt("id"),
            rs.getInt("room_number"),
            rs.getString("type"),
            rs.getDouble("price"),
            rs.getBoolean("available"));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void update(Room room) {
    String sql = "UPDATE rooms SET room_number=? type=?, price=?, available=? WHERE id=?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, room.getRoomNumber());
      stmt.setString(2, room.getType());
      stmt.setDouble(3, room.getPrice());
      stmt.setBoolean(4, room.isAvailable());
      stmt.setInt(5, room.getId());

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
