package org.hotel.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.hotel.db.Database;

public class DashboardDAO {

  private int queryInt(String sql) {
    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      if (rs.next())
        return rs.getInt(1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  private double queryDouble(String sql) {
    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      if (rs.next())
        return rs.getDouble(1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }

  private Object[][] queryRows(String sql, int cols) {
    List<Object[]> rows = new ArrayList<>();

    try (
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Object[] r = new Object[cols];
        for (int i = 0; i < cols; i++)
          r[i] = rs.getObject(i + 1);
        rows.add(r);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return rows.toArray(new Object[0][0]);
  }

  public int getTotalRoomsCount() {
    return queryInt("SELECT COUNT(*) FROM rooms");
  }

  public int getOccupiedRoomsCount() {
    return queryInt("""
          SELECT COUNT(DISTINCT room_id)
          FROM bookings
          WHERE status = 'CHECKED_IN'
        """);
  }

  public int getAvailableRoomsCount() {
    return queryInt("""
          SELECT COUNT(*)
          FROM rooms
          WHERE is_available = 1
        """);
  }

  public int getArrivalsTodayCount() {
    return queryInt("""
          SELECT COUNT(*)
          FROM bookings
          WHERE status = 'RESERVED'
          AND check_in = date('now')
        """);
  }

  public int getDeparturesTodayCount() {
    return queryInt("""
          SELECT COUNT(*)
          FROM bookings
          WHERE status = 'CHECKED_IN'
          AND check_out = date('now')
        """);
  }

  public double getTodayEarnings() {
    return queryDouble("""
          SELECT COALESCE(SUM(total_price), 0)
          FROM bookings
          WHERE check_in = date('now')
          AND status IN ('CHECKED_IN', 'CHECKED_OUT')
        """);
  }

  public double getMonthToDateEarnings() {
    return queryDouble("""
          SELECT COALESCE(SUM(total_price), 0)
          FROM bookings
          WHERE date(check_in) >= date('now','start of month')
          AND date(check_in) <= date('now')
          AND status IN ('CHECKED_IN', 'CHECKED_OUT')
        """);
  }

  public Object[][] getArrivalsTodayRows() {
    return queryRows("""
          SELECT
            b.id,
            c.name,
            r.room_number,
            b.check_in,
            CAST((julianday(b.check_out) - julianday(b.check_in)) AS INTEGER) AS nights
          FROM bookings b
          JOIN customers c ON c.id = b.customer_id
          JOIN rooms r ON r.id = b.room_id
          WHERE b.status = 'RESERVED'
          AND b.check_in = date('now')
          ORDER BY b.id DESC
        """, 5);
  }

  public Object[][] getDeparturesTodayRows() {
    return queryRows("""
          SELECT
            b.id,
            c.name,
            r.room_number,
            b.check_out,
            b.total_price
          FROM bookings b
          JOIN customers c ON c.id = b.customer_id
          JOIN rooms r ON r.id = b.room_id
          WHERE b.status = 'CHECKED_IN'
          AND b.check_out = date('now')
          ORDER BY b.id DESC
        """, 5);
  }
}
