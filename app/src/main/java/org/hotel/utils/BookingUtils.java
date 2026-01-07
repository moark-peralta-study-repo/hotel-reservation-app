package org.hotel.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hotel.dto.BookingRowDTO;
import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;

public class BookingUtils {
  private BookingUtils() {
  }

  public static Booking mapRowToBooking(ResultSet rs) throws SQLException {
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

  public static BookingRowDTO mapRowToDTO(ResultSet rs) throws SQLException {
    return new BookingRowDTO(
        rs.getInt("booking_id"),
        rs.getString("customer_name"),
        rs.getInt("room_number"),
        rs.getString("check_in"),
        rs.getString("check_out"),
        rs.getDouble("total_price"),
        BookingStatus.valueOf(rs.getString("status")));
  }
}
