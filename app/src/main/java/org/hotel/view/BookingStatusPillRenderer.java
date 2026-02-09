package org.hotel.view;

import java.awt.Color;

import org.hotel.model.BookingStatus;

public class BookingStatusPillRenderer
    extends EnumPillRenderer<BookingStatus> {

  @Override
  protected Color getBgColor(BookingStatus status) {
    return switch (status) {
      case RESERVED -> Color.decode("#BBDEFB");
      case CHECKED_IN -> Color.decode("#C8E6C9");
      case CHECKED_OUT -> Color.decode("#EEEEEE");
      case CANCELLED -> Color.decode("#FFB8B8");
    };
  }

  @Override
  protected Color getTextColor(BookingStatus status) {
    return switch (status) {
      case RESERVED -> Color.decode("#1565C0");
      case CHECKED_IN -> Color.decode("#2E7D32");
      case CHECKED_OUT -> Color.decode("#616161");
      case CANCELLED -> Color.decode("#C62828");
    };
  }
}
