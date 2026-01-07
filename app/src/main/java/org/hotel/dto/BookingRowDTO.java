package org.hotel.dto;

import org.hotel.model.BookingStatus;

public class BookingRowDTO {
  private int bookingId;
  private String customerName;
  private int roomNumber;
  private String checkIn;
  private String checkOut;
  private double totalPrice;

  public int getBookingId() {
    return bookingId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public int getRoomNumber() {
    return roomNumber;
  }

  public String getCheckIn() {
    return checkIn;
  }

  public String getCheckOut() {
    return checkOut;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public BookingStatus getStatus() {
    return status;
  }

  private BookingStatus status;

  public BookingRowDTO(
      int bookingId,
      String customerName,
      int roomNumber,
      String checkIn,
      String checkOut,
      double totalPrice,
      BookingStatus status) {
    this.bookingId = bookingId;
    this.customerName = customerName;
    this.roomNumber = roomNumber;
    this.checkIn = checkIn;
    this.checkOut = checkOut;
    this.totalPrice = totalPrice;
    this.status = status;
  }
}
