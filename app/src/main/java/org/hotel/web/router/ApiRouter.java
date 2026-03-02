package org.hotel.web.router;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.hotel.web.controller.BookingHttpController;
import org.hotel.web.util.HttpUtils;

public class ApiRouter implements HttpHandler {

  private final BookingHttpController bookingController = new BookingHttpController();

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    String fullPath = exchange.getRequestURI().getPath();
    HttpUtils.addCorsHeaders(exchange);

    if (!fullPath.startsWith("/api/bookings")) {
      exchange.sendResponseHeaders(404, -1);
      return;
    }

    String subPath = fullPath.substring("/api/bookings".length());

    if (subPath.isEmpty()) {
      subPath = "/";
    }

    exchange.setAttribute("subPath", subPath);

    bookingController.handle(exchange);
  }
}
