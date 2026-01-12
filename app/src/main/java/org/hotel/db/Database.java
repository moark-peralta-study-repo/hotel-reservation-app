package org.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

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
      seedRooms(stmt);
      seedBookingAuto(stmt);
      seedCustomers(stmt);

      System.out.println("Database Initialized Successfully.");
    } catch (SQLException e) {
      System.out.println("Database initialization failed.");
      e.printStackTrace();
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

  private static void seedBookingAuto(Statement stmt) throws SQLException {
    LocalDate today = LocalDate.now();

    for (int i = 1; i <= 10; i++) {
      int customerId = (i % 6) + 1;
      int roomId = (i % 50) + 1;

      LocalDate checkIn = today.plusDays((i % 10) - 5); // 5 days before or after today
      LocalDate checkOut = checkIn.plusDays(2);

      String status;
      if (today.isBefore(checkIn)) {
        status = "RESERVED";
      } else if (!today.isBefore(checkOut)) {
        status = "CHECKED_OUT";
      } else {
        status = "CHECKED_IN";
      }

      double price = switch (roomId % 3) {
        case 1 -> 1200;
        case 2 -> 1800;
        default -> 3500;
      };

      stmt.executeUpdate("""
              INSERT INTO bookings (customer_id, room_id, check_in, check_out, total_price, status)
              VALUES (%d, %d, '%s', '%s', %.2f, '%s')
          """.formatted(customerId, roomId, checkIn, checkOut, price, status));
    }
  }

  private static void seedRooms(Statement stmt) throws SQLException {

    var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM rooms");
    if (rs.next() && rs.getInt("count") > 0) {
      return; // already seeded
    }

    for (int floor = 1; floor <= 5; floor++) {
      for (int room = 1; room <= 10; room++) {

        int roomNumber = floor * 100 + room;

        String type;
        double price;

        switch (roomNumber % 3) {
          case 1 -> {
            type = "Single";
            price = 1200;
          }
          case 2 -> {
            type = "Double";
            price = 1800;
          }
          default -> {
            type = "Suite";
            price = 3500;
          }
        }

        stmt.executeUpdate(
            """
                INSERT INTO rooms (room_number, type, price, is_available)
                VALUES (%d, '%s', %.2f, 1)
                """.formatted(roomNumber, type, price));
      }
    }

    System.out.println("Seeded: 50 Rooms");
  }

  private static void seedCustomers(Statement stmt) throws SQLException {
    var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM customers");
    if (rs.next() && rs.getInt("count") > 0)
      return;

    String[] names = { "Alice", "Bob", "Charlie", "David", "Eve", "Frank" };
    for (String name : names) {
      stmt.executeUpdate("INSERT INTO customers (name, phone, email) " +
          "VALUES ('" + name + "', '1234567890', '" + name.toLowerCase() + "@example.com')");
    }
    System.out.println("Seeded: 6 Customers");
  }
}
