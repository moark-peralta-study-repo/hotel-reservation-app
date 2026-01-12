package org.hotel.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.hotel.db.Database;

public class DashboardDAO {

  private int count(String sql) {
    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()

    ) {

      if (rs.next())
        return rs.getInt(1);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

  public int getReservationCount() {
    return count("""
          SELECT COUNT(*)
          FROM bookings
          WHERE status = 'RESERVED'
        """);
  }

  public int getCheckedInCount() {
    return count("""
          SELECT COUNT(*)
          FROM bookings
          WHERE status = 'CHECKED_IN'
        """);
  }

  public int getTodayCheckInCount() {
    return count("""
          SELECT COUNT(*)
          FROM bookings
          WHERE status = 'RESERVED'
          AND check_in = date('now')
        """);
  }

  public int getTodayCheckOutCount() {
    return count("""
          SELECT COUNT(*)
          FROM bookings
          WHERE status = 'CHECKED_IN'
          AND check_out = date('now')
        """);
  }
}
