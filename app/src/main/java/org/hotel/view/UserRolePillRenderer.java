package org.hotel.view;

import java.awt.Color;

import org.hotel.model.UserRole;

public class UserRolePillRenderer
    extends EnumPillRenderer<UserRole> {

  @Override
  protected Color getBgColor(UserRole role) {
    return switch (role) {
      case ADMIN -> Color.decode("#E1BEE7"); 
      case RECEPTIONIST -> Color.decode("#FFF9C4"); 
    };
  }

  @Override
  protected Color getTextColor(UserRole role) {
    return switch (role) {
      case ADMIN -> Color.decode("#6A1B9A");
      case RECEPTIONIST -> Color.decode("#F57F17");
    };
  }
}
