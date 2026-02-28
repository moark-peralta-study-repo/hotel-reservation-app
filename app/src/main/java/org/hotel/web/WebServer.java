package org.hotel.web;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import org.hotel.web.controller.BookingHttpController;

public class WebServer {
  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    server.createContext("/", exchange -> {
      String response = "Server is working!";

      exchange.sendResponseHeaders(200, response.getBytes().length);
      exchange.getResponseBody().write(response.getBytes());
      exchange.getResponseBody().close();
    });

    server.createContext("/api/bookings", new BookingHttpController());

    server.setExecutor(null);
    server.start();
    System.out.println("Server started at http://localhost:8080");
  }
}
