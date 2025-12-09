package org.hotel.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.hotel.db.Database;
import org.hotel.model.Booking;

public class BookingsDAO {

  public void insert(Booking booking) {
    String sql = "INSERT INTO bookings (customer_id, room_id, check_out, check_in, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, booking.getCustomerId());
      pstmt.setInt(2, booking.getRoomId());
      pstmt.setString(3, booking.getCheckOut());
      pstmt.setString(4, booking.getCheckIn());
      pstmt.setDouble(5, booking.getTotalPrice());
      pstmt.setString(6, booking.getStatus());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Booking> getAll() {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT * FROM bookings";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Booking booking = new Booking(
            rs.getInt("id"),
            rs.getInt("customer_id"),
            rs.getInt("room_id"),
            rs.getString("check_out"),
            rs.getString("check_in"),
            rs.getDouble("total_price"),
            rs.getString("status"));

        bookings.add(booking);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return bookings;
  }
}
