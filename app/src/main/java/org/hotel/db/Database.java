package org.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database {
  private static final String URL = "jdbc:sqlite:hotel.db";

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL);
  }

  public static void initializeDatabase() {
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {

      stmt.execute("PRAGMA busy_timeout = 5000");
      stmt.execute("PRAGMA journal_mode = WAL");

      createTables(stmt);
      seedUsers(stmt);
      seedRooms(stmt);
      seedCustomers(conn, stmt);
      seedBookingsAuto(conn, stmt);

      System.out.println("Database Initialized Successfully.");
    } catch (SQLException e) {
      System.out.println("Database initialization failed.");
      e.printStackTrace();
    }
  }

  private static void createTables(Statement stmt) throws SQLException {
    String sqlUsers = """
          CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            first_name TEXT NOT NULL,
            last_name TEXT NOT NULL,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            role TEXT NOT NULL
          );
        """;

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
            status TEXT DEFAULT 'RESERVED',
            FOREIGN KEY(customer_id) REFERENCES customers(id),
            FOREIGN KEY(room_id) REFERENCES rooms(id)
          );
        """;

    stmt.execute(sqlCustomers);
    stmt.execute(sqlRooms);
    stmt.execute(sqlBookings);
    stmt.execute(sqlUsers);

    System.out.println("Tables Created");
  }

  private static void seedUsers(Statement stmt) throws SQLException {
    var rs = stmt.executeQuery("""
          SELECT COUNT(*) AS count
          FROM users
          WHERE username = 'admin_001'
        """);

    if (rs.next() && rs.getInt("count") > 0)
      return;

    stmt.executeUpdate("""
          INSERT INTO users (first_name, last_name, username, password, role)
          VALUES ('System', 'Admin', 'admin_001', 'admin001', 'ADMIN')
        """);

    System.out.println("Seeded: Admin User (admin_001)");
  }

  private static void seedRooms(Statement stmt) throws SQLException {
    var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM rooms");
    if (rs.next() && rs.getInt("count") > 0)
      return;

    for (int floor = 1; floor <= 5; floor++) {
      for (int room = 1; room <= 10; room++) {

        int roomNumber = (floor * 100) + room;

        String type;
        double price;

        switch (room) {
          case 1, 2, 3 -> {
            type = "Single";
            price = 1200;
          }
          case 4, 5, 6, 7 -> {
            type = "Double";
            price = 1800;
          }
          default -> {
            type = "Suite";
            price = 3500;
          }
        }

        stmt.executeUpdate("""
              INSERT INTO rooms (room_number, type, price, is_available)
              VALUES (%d, '%s', %.2f, 1)
            """.formatted(roomNumber, type, price));
      }
    }

    System.out.println("Seeded: 50 Rooms (101–110 … 501–510)");
  }

  private static void seedCustomers(Connection conn, Statement stmt) throws SQLException {
    var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM customers");
    if (rs.next() && rs.getInt("count") > 0)
      return;

    String[] firstNames = {
        "Mark", "Lester", "Anna", "John", "Maria", "Paolo", "Andrea", "Kevin", "Patricia", "Noel",
        "Joshua", "Bianca", "Carlo", "Diane", "Elaine", "Francis", "Grace", "Hannah", "Ivan", "Jasmine"
    };

    String[] lastNames = {
        "Reyes", "Santos", "Cruz", "Lim", "Tan", "Go", "Dela Cruz", "Garcia", "Navarro", "Mendoza",
        "Flores", "Aquino", "Ramos", "Villanueva", "Castillo", "Torres", "Domingo", "Bautista"
    };

    Random rnd = new Random(42);

    try (PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO customers (name, phone, email) VALUES (?, ?, ?)")) {

      for (int i = 0; i < 40; i++) {
        String fname = firstNames[rnd.nextInt(firstNames.length)];
        String lname = lastNames[rnd.nextInt(lastNames.length)];
        String name = fname + " " + lname;

        String phone = "09" + (8 + rnd.nextInt(2)) + String.format("%08d", rnd.nextInt(100_000_000));
        String email = (fname + "." + lname + (100 + i) + "@example.com").toLowerCase();

        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.addBatch();
      }

      ps.executeBatch();
    }

    System.out.println("Seeded: 40 Customers");
  }

  private static void seedBookingsAuto(Connection conn, Statement stmt) throws SQLException {

    var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM bookings");
    if (rs.next() && rs.getInt("count") > 0)
      return;

    LocalDate today = LocalDate.now();
    Random rnd = new Random(99);

    List<Integer> customerIds = new ArrayList<>();
    try (var rsC = stmt.executeQuery("SELECT id FROM customers")) {
      while (rsC.next())
        customerIds.add(rsC.getInt("id"));
    }

    List<Integer> roomIds = new ArrayList<>();
    try (var rsR = stmt.executeQuery("SELECT id FROM rooms")) {
      while (rsR.next())
        roomIds.add(rsR.getInt("id"));
    }

    try (PreparedStatement ps = conn.prepareStatement("""
          INSERT INTO bookings (customer_id, room_id, check_in, check_out, total_price, status)
          VALUES (?, ?, ?, ?, ?, ?)
        """);
        PreparedStatement setRoomBusy = conn.prepareStatement("""
              UPDATE rooms SET is_available = 0 WHERE id = ?
            """)) {

      int totalBookings = 0;

      for (int roomId : roomIds) {

        int bookingsPerRoom = 3 + rnd.nextInt(3); // 3–5 per room

        for (int i = 0; i < bookingsPerRoom; i++) {
          int customerId = customerIds.get(rnd.nextInt(customerIds.size()));

          int type = rnd.nextInt(4);
          LocalDate checkIn;
          LocalDate checkOut;
          String status;

          switch (type) {
            case 0 -> {
              // Past stay (checked out)
              checkOut = today.minusDays(1 + rnd.nextInt(10));
              checkIn = checkOut.minusDays(1 + rnd.nextInt(4));
              status = "CHECKED_OUT";
            }

            case 1 -> {
              // Currently checked in
              checkIn = today.minusDays(rnd.nextInt(2));
              checkOut = today.plusDays(1 + rnd.nextInt(3));
              status = "CHECKED_IN";

              setRoomBusy.setInt(1, roomId);
              setRoomBusy.addBatch();
            }

            case 2 -> {
              // Future reservation
              checkIn = today.plusDays(1 + rnd.nextInt(14));
              checkOut = checkIn.plusDays(1 + rnd.nextInt(4));
              status = "RESERVED";
            }

            default -> {
              // ❗ NO-SHOW (auto-cancel target)
              checkIn = today.minusDays(1 + rnd.nextInt(5));
              checkOut = checkIn.plusDays(1 + rnd.nextInt(3));
              status = "RESERVED";
            }
          }

          double nightly = switch (roomId % 3) {
            case 1 -> 1200.0;
            case 2 -> 1800.0;
            default -> 3500.0;
          };

          long nights = Math.max(1, checkOut.toEpochDay() - checkIn.toEpochDay());
          double totalPrice = nightly * nights;

          ps.setInt(1, customerId);
          ps.setInt(2, roomId);
          ps.setString(3, checkIn.toString());
          ps.setString(4, checkOut.toString());
          ps.setDouble(5, totalPrice);
          ps.setString(6, status);
          ps.addBatch();

          totalBookings++;
        }
      }

      ps.executeBatch();
      setRoomBusy.executeBatch();

      System.out.println("Seeded: " + totalBookings + " Bookings (includes auto-cancel candidates)");
    }
  }
}
