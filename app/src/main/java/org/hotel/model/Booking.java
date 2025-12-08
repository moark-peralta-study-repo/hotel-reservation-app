package org.hotel.model;

public class Booking {
  private int id;
  private int customerId;
  private int roomId;
  private String checkOut;
  private String checkIn;
  private double totalPrice;

  public Booking(int id, int customerId, int roomId, String checkOut, String checkIn, double totalPrice) {
    this.id = id;
    this.customerId = customerId;
    this.roomId = roomId;
    this.checkOut = checkOut;
    this.checkIn = checkIn;
    this.totalPrice = totalPrice;
  }

  public Booking(int customerId, int roomId, String checkOut, String checkIn, double totalPrice) {
    this.customerId = customerId;
    this.roomId = roomId;
    this.checkOut = checkOut;
    this.checkIn = checkIn;
    this.totalPrice = totalPrice;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getCustomerId() {
    return customerId;
  }

  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public int getRoomId() {
    return roomId;
  }

  public void setRoomId(int roomId) {
    this.roomId = roomId;
  }

  public String getCheckOut() {
    return checkOut;
  }

  public void setCheckOut(String checkOut) {
    this.checkOut = checkOut;
  }

  public String getCheckIn() {
    return checkIn;
  }

  public void setCheckIn(String checkIn) {
    this.checkIn = checkIn;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

}
