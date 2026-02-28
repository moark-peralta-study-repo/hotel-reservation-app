package org.hotel.web.util;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class HttpUtils {

  public static void addCorsHeaders(HttpExchange exchange) {
    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
  }

  public static void sendError(HttpExchange exchange, int status, String message) throws IOException {
    String error = "{\"error\":\"" + message + "\"}";
    sendJson(exchange, status, error);
  }

  public static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
    byte[] bytes = json.getBytes();
  }

  public static int extractId(String path) {
    return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
  }
}
