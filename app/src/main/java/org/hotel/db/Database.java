package org.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
  private static final String URL = "jdbc:sqlite:hotel.db";

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL);
  }

  public static void initializeDatabase() {

    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {

      createTables(stmt);
      // stmt.executeUpdate("DELETE FROM rooms");
      // stmt.executeUpdate("DELETE FROM customers");
      // stmt.executeUpdate("DELETE FROM bookings");
      seedAll(stmt);

      System.out.println("Database Initialized Successfully.");
    } catch (SQLException e) {
      System.out.println("Database initialization failed.");
      e.printStackTrace();
    }
  }

  private static void seedAll(Statement stmt) throws SQLException {
    ResultSet rsRooms = stmt.executeQuery("SELECT COUNT(*) AS count FROM rooms");

    if (rsRooms.next() && rsRooms.getInt("count") == 0) {

      String insertRooms = """
            INSERT INTO rooms (room_number, type, price, is_available) VALUES
              (101, 'Single', 1200, 1),
              (102, 'Single', 1200, 1),
              (103, 'Single', 1200, 1),
              (104, 'Single', 1200, 1),
              (105, 'Single', 1200, 1),
              (106, 'Single', 1200, 1),

              (201, 'Double', 1800, 1),
              (202, 'Double', 1800, 1),
              (203, 'Double', 1800, 1),
              (204, 'Double', 1800, 1),
              (205, 'Double', 1800, 1),
              (206, 'Double', 1800, 1),

              (301, 'Suite', 3500, 1),
              (302, 'Suite', 3500, 1),
              (303, 'Suite', 3500, 1),
              (304, 'Suite', 3500, 1),
              (305, 'Suite', 3500, 1),
              (306, 'Suite', 3500, 1);
          """;

      stmt.executeUpdate(insertRooms);
      System.out.println("Seeded: Rooms");
    }

    ResultSet rsCustomers = stmt.executeQuery("SELECT COUNT(*) AS count FROM customers");
    if (rsCustomers.next() && rsCustomers.getInt("count") == 0) {
      String insertCustomers = """
            INSERT INTO customers (name, phone, email) VALUES
              ('Juan Dela Cruz', '09696969696', 'juan@example.com'),
              ('Maria Santos', '09999999999', 'maria@example.com'),
              ('Jessie Prado', '09888888888', 'jessie@example.com'),
              ('Juan Dela Crus', '09696969697', 'juan1@example.com'),
              ('Marla Santos', '09999999990', 'maria1@example.com'),
              ('Love Prado', '09388888888', 'jessie1@example.com');
          """;

      stmt.execute(insertCustomers);
      System.out.println("Seeded: Customers");
    }

    ResultSet rsBookings = stmt.executeQuery("SELECT COUNT(*) AS count FROM bookings");

    if (rsBookings.next() && rsBookings.getInt("count") == 0) {

      String insertBookings = """
            INSERT INTO bookings (customer_id, room_id, check_in, check_out, total_price, status) VALUES
              (1, 101, '2025-12-01', '2025-12-03', 2400, 'reserved'),
              (2, 201, '2025-12-05', '2025-12-06', 1800, 'reserved'),
              (3, 301, '2025-12-10', '2025-12-12', 7000, 'checked_out'),
              (4, 101, '2026-12-01', '2026-12-03', 2400, 'checked_in'),
              (5, 201, '2026-12-05', '2026-12-06', 1800, 'checked_out'),
              (6, 301, '2026-12-10', '2026-12-12', 7000, 'reserved');
          """;
      stmt.execute(insertBookings);
      System.out.println("Seeded: Bookings");
    }
  }

  private static void createTables(Statement stmt) throws SQLException {
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
                room_id INTEGER,
                check_in TEXT,
                check_out TEXT,
                total_price REAL,
                status TEXT default "reserved",
                FOREIGN KEY(customer_id) REFERENCES customers(id),
                FOREIGN KEY(room_id) REFERENCES rooms(id)
            );
        """;

    stmt.execute(sqlCustomers);
    stmt.execute(sqlRooms);
    stmt.execute(sqlBookings);

    System.out.println("Tables Created");
  }
}
