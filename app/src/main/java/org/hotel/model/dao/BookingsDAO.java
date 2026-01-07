package org.hotel.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hotel.db.Database;
import org.hotel.dto.BookingRowDTO;
import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;
import org.hotel.utils.BookingUtils;

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

  public void cancelReservation(Booking booking) {
    String sql = "UPDATE bookings SET status = ? WHERE id = ?";

    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, BookingStatus.CANCELLED.name());
      stmt.setInt(2, booking.getId());
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

  // DTO getAll()
  public List<BookingRowDTO> getAllRows() {
    String sql = """
          SELECT
            b.id AS booking_id,
            c.name AS customer_name,
            r.room_number AS room_number,
            b.check_in,
            b.check_out,
            b.total_price,
            b.status
          FROM bookings b
          JOIN customers c ON b.customer_id = c.id
          JOIN rooms r ON b.room_id = r.id
          ORDER BY b.check_in
        """;

    List<BookingRowDTO> rows = new ArrayList<>();

    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        rows.add(BookingUtils.mapRowToDTO(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return rows;
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
  // Arrival today or late checkin
  public List<Booking> getPendingCheckIn() {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT * FROM bookings WHERE status = 'RESERVED' AND check_in <= date('now')";

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

  public List<BookingRowDTO> getPendingCheckInRows() {
    String sql = """
        SELECT
          b.id AS booking_id,
          c.name AS customer_name,
          r.room_number AS room_number,
          b.check_in,
          b.check_out,
          b.total_price,
          b.status,
        FROM bookings b
        JOIN customers c ON b.customer_id = c.id
        JOIN rooms r ON b.room_id = r.id
        WHERE b.status = 'RESERVED' AND b.check_in <= date('now')
        """;
    List<BookingRowDTO> rows = new ArrayList<>();

    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        rows.add(BookingUtils.mapRowToDTO(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return rows;
  }

  public List<Booking> getReservedBookings() {
    List<Booking> bookings = new ArrayList<>();

    String sql = "SELECT * FROM bookings WHERE status = 'RESERVED' AND check_in > date('now')";

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
    String sql = "SELECT * FROM bookings WHERE status = 'CHECKED_IN' ";

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
    String sql = "UPDATE bookings SET status = ? WHERE id = ?";

    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, BookingStatus.CHECKED_IN.name());
      stmt.setInt(2, booking.getId());
      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void checkOutCustomer(Booking booking) {
    String sql = "UPDATE bookings SET status = ? WHERE id = ?";

    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, BookingStatus.CHECKED_OUT.name());
      stmt.setInt(2, booking.getId());
      stmt.executeUpdate();

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
        return BookingUtils.mapRowToBooking(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  public List<BookingRowDTO> findAllForTable() {

    List<BookingRowDTO> rows = new ArrayList<>();

    String sql = """
          SELECT
            b.id AS booking_id,
            c.name AS customer_name,
            r.room_number,
            b.check_in,
            b.check_out,
            b.total_price,
            b.status
          FROM bookings b
          JOIN customers c ON b.customer_id = c.id
          JOIN rooms r ON b.room_id = r.id
          ORDER BY b.check_in
        """;

    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        rows.add(BookingUtils.mapRowToDTO(rs));

      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return rows;
  }
}
