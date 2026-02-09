package org.hotel.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

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

  public static JPanel buildHeader(String titleText, String subtitleText) {
    Color BG = Color.decode("#f9fafb");
    Color MUTED = Color.decode("#6b7280");
    Color TEXT = Color.decode("#111827");

    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(BG);
    header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

    JLabel title = new JLabel(titleText);
    title.setFont(new Font("Poppins", Font.BOLD, 28));
    title.setForeground(TEXT);

    JLabel subtitle = new JLabel(subtitleText);
    subtitle.setFont(new Font("Poppins", Font.PLAIN, 14));
    subtitle.setForeground(MUTED);

    JPanel left = new JPanel();
    left.setBackground(BG);
    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
    left.add(title);
    left.add(Box.createVerticalStrut(4));
    left.add(subtitle);

    header.add(left, BorderLayout.WEST);
    return header;
  }
}
