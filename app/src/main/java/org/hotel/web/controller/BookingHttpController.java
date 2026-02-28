package org.hotel.web.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.hotel.dto.BookingRowDTO;
import org.hotel.model.Booking;
import org.hotel.model.dao.BookingsDAO;

public class BookingHttpController implements HttpHandler {

  private final BookingsDAO bookingsDAO = new BookingsDAO();
  private final ObjectMapper mapper = new ObjectMapper();

  public void handle(HttpExchange exchange) throws IOException {
    try {
      String method = exchange.getRequestMethod();

      mapper.registerModule(new JavaTimeModule());

      if ("GET".equalsIgnoreCase(method)) {
        handleGet(exchange);
      } else if ("POST".equalsIgnoreCase(method)) {
        handlePost(exchange);
      } else {
        exchange.sendResponseHeaders(405, -1);
      }
    } catch (Exception e) {
      e.printStackTrace();

      String error = "{\"errpr\":\"" + e.getMessage() + "\"}";
      byte[] bytes = error.getBytes();

      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(500, bytes.length);
      exchange.getResponseBody().write(bytes);
      exchange.getResponseBody().close();
    }
  }

  private void handlePost(HttpExchange exchange) throws IOException {
    Booking booking = mapper.readValue(exchange.getRequestBody(), Booking.class);

    bookingsDAO.insert(booking);

    exchange.sendResponseHeaders(201, -1);
  }

  private void handleGet(HttpExchange exchange) throws IOException {
    List<BookingRowDTO> bookings = bookingsDAO.findAllForTable();

    String json = mapper.writeValueAsString(bookings);

    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, json.getBytes().length);
    exchange.getResponseBody().write(json.getBytes());
    exchange.getResponseBody().close();
  }
}
