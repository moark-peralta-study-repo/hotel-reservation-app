package org.hotel.model;

public class Room {
  private int id;
  private int roomNumber;
  private RoomType type;
  private double price;
  private boolean isAvailable;

  public Room(int id, int roomNumber, RoomType type, double price, boolean isAvailable) {
    this.id = id;
    this.roomNumber = roomNumber;
    this.type = type;
    this.price = price;
    this.isAvailable = isAvailable;
  }

  public Room(int roomNumber, RoomType type, double price, boolean isAvailable) {
    this.roomNumber = roomNumber;
    this.type = type;
    this.price = price;
    this.isAvailable = isAvailable;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getRoomNumber() {
    return roomNumber;
  }

  public void setRoomNumber(int roomNumber) {
    this.roomNumber = roomNumber;
  }

  public RoomType getType() {
    return type;
  }

  public void setType(RoomType type) {
    this.type = type;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean isAvailable) {
    this.isAvailable = isAvailable;
  }

}
