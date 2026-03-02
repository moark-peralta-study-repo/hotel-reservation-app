package org.hotel.web.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;
import org.hotel.model.dao.BookingsDAO;
import org.hotel.web.util.HttpUtils;

public class BookingHttpController implements HttpHandler {

  private final BookingsDAO bookingsDAO = new BookingsDAO();
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      String method = exchange.getRequestMethod();
      String path = (String) exchange.getAttribute("subPath");

      if ("OPTIONS".equalsIgnoreCase(method)) {
        exchange.sendResponseHeaders(204, -1);
        return;
      }

      if ("/".equals(path)) {

        switch (method) {
          case "GET" -> handleGet(exchange);
          case "POST" -> handlePost(exchange);
          default -> exchange.sendResponseHeaders(405, -1);
        }

      } else if (path.matches("/\\d+/(cancel|check-in|check-out)")) {

        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[1]);
        String action = parts[2];

        switch (action) {
          case "cancel" -> handleCancelBookings(exchange, id);
          case "check-in" -> handleCheckIn(exchange, id);
          case "check-out" -> handleCheckOut(exchange, id);
        }

      } else if (path.matches("/\\d+")) {

        int id = Integer.parseInt(path.substring(1));

        switch (method) {
          case "GET" -> handleGetBookingById(exchange, id);
          case "PUT" -> handlePut(exchange, id);
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

  private Object handleCheckOut(HttpExchange exchange, int id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleCheckOut'");
  }

  private Object handleCheckIn(HttpExchange exchange, int id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleCheckIn'");
  }

  private void handleCancelBookings(HttpExchange exchange, int id) throws IOException {

    Booking existing = bookingsDAO.getById(id);

    if (existing == null) {
      HttpUtils.sendError(exchange, 404, "Booking with id " + id + " not found.");
      return;
    }

    if (existing.getStatus() != BookingStatus.RESERVED) {
        HttpUtils.sendError(exchange, 409, "Only RESERVED bookings can be cancelled.");
        return;
    }

    bookingsDAO.cancelReservation(existing);

    exchange.sendResponseHeaders(204, -1);
  }

  private void handlePut(HttpExchange exchange, int id) throws IOException {
    Booking updatedBooking = mapper.readValue(exchange.getRequestBody(), Booking.class);

    Booking existing = bookingsDAO.getById(id);

    if (existing == null) {
      HttpUtils.sendError(exchange, 404, "Booking with id " + id + " not found.");
      return;
    } else {
      updatedBooking.setId(id);
      bookingsDAO.update(updatedBooking);

      mapper.writeValueAsString(updatedBooking);
      exchange.sendResponseHeaders(204, -1);
    }
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
    List<Booking> bookings = bookingsDAO.getAll();

    String json = mapper.writeValueAsString(bookings);

    HttpUtils.sendJson(exchange, 200, json);
  }
}
