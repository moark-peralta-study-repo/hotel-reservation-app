package org.hotel.model.dao;

import java.awt.print.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.hotel.utils.BookingUtils;

import org.hotel.db.Database;
import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;

public class BookingsDAO {
  public void update(Booking booking) {
    String sql = """
          UPDATE bookings
          SET customer_id = ?, room_id = ?, check_in = ?, check_out = ?, total_price = ?, status = ?
          WHERE id = ?
        """;

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, booking.getCustomerId());
      stmt.setInt(2, booking.getRoomId());
      stmt.setString(3, booking.getCheckIn());
      stmt.setString(4, booking.getCheckOut());
      stmt.setDouble(5, booking.getTotalPrice());
      stmt.setString(6, booking.getStatus().name());
      stmt.setInt(7, booking.getId());

      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void cancel(int id) {
    String sql = """
          UPDATE bookings SET status = 'cancelled' WHERE id = ?
        """;

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);
      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void insert(Booking booking) {
    String sql = "INSERT INTO bookings (customer_id, room_id, check_in, check_out, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, booking.getCustomerId());
      pstmt.setInt(2, booking.getRoomId());
      pstmt.setString(3, booking.getCheckIn());
      pstmt.setString(4, booking.getCheckOut());
      pstmt.setDouble(5, booking.getTotalPrice());
      pstmt.setString(6, booking.getStatus().name());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Use this method when click the Bookings NavButton
  public List<Booking> getAll() {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT * FROM bookings";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        bookings.add(BookingUtils.mapRowToBooking(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return bookings;
  }

  // For checkIn view
  public List<Booking> getPendingCheckIn() {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT * FROM bookings WHERE status = 'reserved' AND check_in <= date('now')";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {

        bookings.add(BookingUtils.mapRowToBooking(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return bookings;
  }

  public List<Booking> getCheckedInBookings() {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT * FROM bookings WHERE status = 'checked_in' ";

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {

        bookings.add(BookingUtils.mapRowToBooking(rs));
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return bookings;
  }

  public void checkInCustomer(Booking booking) {
    String updateBooking = "UPDATE bookings SET status = 'checked_in' WHERE id = ?";
    String updateRoom = "UPDATE rooms SET is_available = 0 WHERE id = ?";

    try (Connection conn = Database.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement bookingStmt = conn.prepareStatement(updateBooking);
          PreparedStatement roomStmt = conn.prepareStatement(updateRoom)) {

        bookingStmt.setInt(1, booking.getId());
        bookingStmt.executeUpdate();

        roomStmt.setInt(1, booking.getRoomId());
        roomStmt.executeUpdate();

        conn.commit();

      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void checkOutCustomer(Booking booking) {
    String updateBooking = "UPDATE bookings SET status = 'checked_out' WHERE id = ?";
    String updateRoom = "UPDATE rooms SET is_available = 1 WHERE id = ?";

    try (Connection conn = Database.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement bookingStmt = conn.prepareStatement(updateBooking);
          PreparedStatement roomStmt = conn.prepareStatement(updateRoom)) {

        bookingStmt.setInt(1, booking.getId());
        bookingStmt.executeUpdate();

        roomStmt.setInt(1, booking.getRoomId());
        roomStmt.executeUpdate();

      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Booking getById(int id) {
    String sql = "SELECT * FROM bookings WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        BookingStatus status = BookingStatus.valueOf(rs.getString("status").toUpperCase());
        return new Booking(
            rs.getInt("id"),
            rs.getInt("customer_id"),
            rs.getInt("room_id"),
            rs.getString("check_in"),
            rs.getString("check_out"),
            rs.getDouble("total_price"),
            status);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}
