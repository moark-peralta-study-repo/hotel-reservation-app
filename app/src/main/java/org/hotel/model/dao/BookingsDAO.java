package org.hotel.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hotel.db.Database;
import org.hotel.dto.BookingRowDTO;
import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;
import org.hotel.utils.BookingUtils;
import org.hotel.view.AddReservationDialog;

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

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, BookingStatus.CANCELLED.name());
      stmt.setInt(2, booking.getId());
      stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void insert(Booking booking) {
    String sql = """
          INSERT INTO bookings (customer_id, room_id, check_in, check_out, total_price, status)
          VALUES (?, ?, ?, ?, ?, ?)
        """;

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

    try (Connection conn = Database.getConnection();
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
          b.status
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

  public List<BookingRowDTO> getReservedBookingRows() {
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
        WHERE b.status = 'RESERVED' AND b.check_in > date('now')
        """;

    List<BookingRowDTO> rows = new ArrayList<>();

    try (Connection conn = Database.getConnection();
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

  public List<Booking> getCheckedInBookings() {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT * FROM bookings WHERE status = 'CHECKED_IN'";

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

  public List<BookingRowDTO> getCheckedInBookingsRow() {
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
        WHERE b.status = 'CHECKED_IN'
        """;

    List<BookingRowDTO> rows = new ArrayList<>();

    try (Connection conn = Database.getConnection();
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

  /**
   * Check-in = update booking status + mark room unavailable.
   * Done in one transaction to avoid desync.
   */
  public void checkInCustomer(Booking booking) {
    String bookingSql = "UPDATE bookings SET status = ? WHERE id = ?";
    String roomSql = "UPDATE rooms SET is_available = 0 WHERE id = ?";

    try (Connection conn = Database.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement bStmt = conn.prepareStatement(bookingSql);
          PreparedStatement rStmt = conn.prepareStatement(roomSql)) {

        bStmt.setString(1, BookingStatus.CHECKED_IN.name());
        bStmt.setInt(2, booking.getId());
        bStmt.executeUpdate();

        rStmt.setInt(1, booking.getRoomId());
        rStmt.executeUpdate();

        conn.commit();

      } catch (SQLException e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Check-out = update booking status + mark room available.
   * Done in one transaction to avoid desync.
   */
  public void checkOutCustomer(Booking booking) {
    String bookingSql = "UPDATE bookings SET status = ? WHERE id = ?";
    String roomSql = "UPDATE rooms SET is_available = 1 WHERE id = ?";

    try (Connection conn = Database.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement bStmt = conn.prepareStatement(bookingSql);
          PreparedStatement rStmt = conn.prepareStatement(roomSql)) {

        bStmt.setString(1, BookingStatus.CHECKED_OUT.name());
        bStmt.setInt(2, booking.getId());
        bStmt.executeUpdate();

        rStmt.setInt(1, booking.getRoomId());
        rStmt.executeUpdate();

        conn.commit();

      } catch (SQLException e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
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
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return BookingUtils.mapRowToBooking(rs);
        }
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

    try (Connection conn = Database.getConnection();
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

  public List<AddReservationDialog.RoomBookingRange> getActiveRoomRanges() {
    String sql = """
          SELECT room_id, check_in, check_out
          FROM bookings
          WHERE status IN ('RESERVED', 'CHECKED_IN')
        """;

    List<AddReservationDialog.RoomBookingRange> ranges = new ArrayList<>();

    try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        int roomId = rs.getInt("room_id");
        LocalDate in = LocalDate.parse(rs.getString("check_in"));
        LocalDate out = LocalDate.parse(rs.getString("check_out"));
        ranges.add(new AddReservationDialog.RoomBookingRange(roomId, in, out));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return ranges;
  }
}
