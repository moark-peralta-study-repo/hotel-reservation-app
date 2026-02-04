package org.hotel.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hotel.db.Database;
import org.hotel.model.Room;
import org.hotel.model.RoomType;

public class RoomDAO {

  public void insert(Room room) {
    String sql = "INSERT INTO rooms (room_number, type, price, is_available) VALUES (?, ?, ?, ?)";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, room.getRoomNumber());
      pstmt.setString(2, room.getType().name());
      pstmt.setDouble(3, room.getPrice());
      pstmt.setInt(4, room.isAvailable() ? 1 : 0);
      pstmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Rooms list should usually show "available TODAY".
   * This computes availability based on overlap with today for statuses RESERVED
   * + CHECKED_IN.
   */
  public List<Room> getAll() {
    List<Room> rooms = new ArrayList<>();
    String sql = "SELECT * FROM rooms";

    // booking overlaps TODAY if:
    // check_in < tomorrow AND check_out > today
    String checkSql = """
          SELECT COUNT(*) AS count
          FROM bookings
          WHERE room_id = ?
            AND status IN ('RESERVED', 'CHECKED_IN')
            AND date(check_in) < date('now','+1 day')
            AND date(check_out) > date('now')
        """;

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        int roomId = rs.getInt("id");

        boolean availableToday = true;
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
          checkStmt.setInt(1, roomId);
          try (ResultSet checkRs = checkStmt.executeQuery()) {
            if (checkRs.next()) {
              availableToday = checkRs.getInt("count") == 0;
            }
          }
        }

        RoomType type = RoomType.valueOf(rs.getString("type").toUpperCase());

        rooms.add(new Room(
            roomId,
            rs.getInt("room_number"),
            type,
            rs.getDouble("price"),
            availableToday));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return rooms;
  }

  /**
   * TRUE if the room has NO overlapping bookings for the given range.
   * Uses [check_in, check_out) logic: checkout day is not occupied.
   *
   * overlap condition:
   * existing.check_in < requested.check_out
   * AND existing.check_out > requested.check_in
   */
  public boolean isRoomAvailableForRange(int roomId, String checkIn, String checkOut) {
    String sql = """
          SELECT COUNT(*) AS cnt
          FROM bookings
          WHERE room_id = ?
            AND status IN ('RESERVED', 'CHECKED_IN')
            AND date(check_in) < date(?)
            AND date(check_out) > date(?)
        """;

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, roomId);
      stmt.setString(2, checkOut);
      stmt.setString(3, checkIn);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next())
          return rs.getInt("cnt") == 0;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  public Room getById(int id) {
    String sql = "SELECT * FROM rooms WHERE id=?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          RoomType type = RoomType.valueOf(rs.getString("type").toUpperCase());
          return new Room(
              rs.getInt("id"),
              rs.getInt("room_number"),
              type,
              rs.getDouble("price"),
              rs.getInt("is_available") == 1);
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  public Room getByRoomNumber(int roomNumber) {
    String sql = "SELECT * FROM rooms WHERE room_number = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, roomNumber);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          RoomType type = RoomType.valueOf(rs.getString("type").toUpperCase());

          return new Room(
              rs.getInt("id"),
              rs.getInt("room_number"),
              type,
              rs.getDouble("price"),
              rs.getInt("is_available") == 1);
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void update(Room room) {
    String sql = "UPDATE rooms SET room_number=?, type=?, price=?, is_available=? WHERE id=?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, room.getRoomNumber());
      stmt.setString(2, room.getType().name());
      stmt.setDouble(3, room.getPrice());
      stmt.setInt(4, room.isAvailable() ? 1 : 0);
      stmt.setInt(5, room.getId());

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void delete(int id) {
    String sql = "DELETE FROM rooms WHERE id=?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Helper for check-in/check-out updates.
   * This is only for "available RIGHT NOW" tracking.
   */
  public void setAvailability(int roomId, boolean available) {
    String sql = "UPDATE rooms SET is_available = ? WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, available ? 1 : 0);
      stmt.setInt(2, roomId);
      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // If you don’t need this, delete it. Kept here but fixed.
  public void add(Room newRoom) {
    String sql = "INSERT INTO rooms (room_number, type, price, is_available) VALUES (?, ?, ?, ?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, newRoom.getRoomNumber());
      stmt.setString(2, newRoom.getType().name());
      stmt.setDouble(3, newRoom.getPrice());
      stmt.setInt(4, newRoom.isAvailable() ? 1 : 0);

      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean isRoomAvailableForRangeExcludingBooking(int roomId, String checkIn, String checkOut,
      int excludeBookingId) {
    String sql = """
          SELECT COUNT(*) AS cnt
          FROM bookings
          WHERE room_id = ?
            AND id <> ?
            AND status IN ('RESERVED', 'CHECKED_IN')
            AND date(check_in) < date(?)
            AND date(check_out) > date(?)
        """;

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, roomId);
      stmt.setInt(2, excludeBookingId);
      stmt.setString(3, checkOut);
      stmt.setString(4, checkIn);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next())
          return rs.getInt("cnt") == 0;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }
}
