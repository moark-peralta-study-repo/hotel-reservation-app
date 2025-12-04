package org.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
  private static final String URL = "jdbc:sqlite:hotel.db";

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL);
  }

  public static void initializeDatabase() {
    String sqlRooms = """
          CREATE TABLE IF NOT EXISTS rooms (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            room_number INTEGER UNIQUE NOT NULL,
            type TEXT,
            price REAL,
            is_available INTEGER DEFAULT 1
        );
        """;

    String sqlCustomers = """
              CREATE TABLE IF NOT EXISTS customers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT,
                email TEXT
            );

        """;

    String sqlBookings = """
              CREATE TABLE IF NOT EXISTS bookings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER,
                check_in TEXT,
                room_id INTEGER,
                check_out TEXT,
                total_price REAL,
                FOREIGN KEY(customer_id) REFERENCES customers(id),
                FOREIGN KEY(room_id) REFERENCES rooms(id)
            );
        """;

    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {

      stmt.execute(sqlCustomers);
      stmt.execute(sqlRooms);
      stmt.execute(sqlBookings);

      System.out.println("Database Initialized.");
    } catch (SQLException e) {
      System.out.println("Database initialization failed.");
      e.printStackTrace();
    }
  }
}
