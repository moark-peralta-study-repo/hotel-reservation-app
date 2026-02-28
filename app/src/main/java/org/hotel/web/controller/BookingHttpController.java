package org.hotel.web.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.hotel.model.Booking;
import org.hotel.model.dao.BookingsDAO;
import org.hotel.web.util.HttpUtils;

public class BookingHttpController implements HttpHandler {

  private final BookingsDAO bookingsDAO = new BookingsDAO();
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      String method = exchange.getRequestMethod();
      String path = exchange.getRequestURI().getPath();

      HttpUtils.addCorsHeaders(exchange);

      if ("OPTIONS".equalsIgnoreCase(method)) {
        exchange.sendResponseHeaders(204, -1);
        return;
      }

      if ("/api/bookings".equals(path)) {

        switch (method.toUpperCase()) {
          case "GET" -> handleGet(exchange);
          case "POST" -> handlePost(exchange);
          default -> exchange.sendResponseHeaders(405, -1);
        }
      } else if (path.matches("/api/bookings/\\d+")) {
        int id = HttpUtils.extractId(path);
        System.out.println("Received request for booking ID: " + id);
        System.out.println("Path" + path);

        switch (method.toUpperCase()) {
          case "GET" -> handleGetBookingById(exchange, id);
          case "PUT" -> handlePut(exchange, id);
          case "DELETE" -> handlePut(exchange, id);
          default -> exchange.sendResponseHeaders(405, -1);
        }
      } else {
        exchange.sendResponseHeaders(404, -1);
      }

    } catch (Exception e) {
      e.printStackTrace();
      HttpUtils.sendError(exchange, 500, e.getMessage());

    }
  }

  private Object handlePut(HttpExchange exchange, int id) {
    return null;
  }

  private void handleGetBookingById(HttpExchange exchange, int id) throws IOException {

    Booking booking = bookingsDAO.getById(id);

    if (booking == null) {
      HttpUtils.sendError(exchange, 404, "Booking with id " + id + " not found");
      return;
    } else {
      String json = mapper.writeValueAsString(booking);
      HttpUtils.sendJson(exchange, 200, json);
    }
  }

  private void handlePost(HttpExchange exchange) throws IOException {
    Booking booking = mapper.readValue(exchange.getRequestBody(), Booking.class);

    bookingsDAO.insert(booking);

    exchange.sendResponseHeaders(201, -1);
  }

  private void handleGet(HttpExchange exchange) throws IOException {
    // List<BookingRowDTO> bookings = bookingsDAO.findAllForTable();
    List<Booking> bookings = bookingsDAO.getAll();

    String json = mapper.writeValueAsString(bookings);

    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, json.getBytes().length);
    exchange.getResponseBody().write(json.getBytes());
    exchange.getResponseBody().close();
  }
}
