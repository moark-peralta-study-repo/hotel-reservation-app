package org.hotel.view;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class DashboardView extends JPanel {

  private JLabel checkedInLabel;
  private JLabel reservedLabel;
  private JLabel todayCheckInLabel;
  private JLabel todayCheckOutLabel;

  public DashboardView() {
    setLayout(new GridLayout(2, 2, 20, 20));
    setBorder(new EmptyBorder(100, 100, 100, 100));
    setBackground(Color.decode("#f9fafb"));

    checkedInLabel = createCard("Checked In");
    reservedLabel = createCard("Reservations");
    todayCheckInLabel = createCard("Today's Check-ins");
    todayCheckOutLabel = createCard("Today's Check-outs");

    add(checkedInLabel);
    add(reservedLabel);
    add(todayCheckInLabel);
    add(todayCheckOutLabel);
  }

  private JLabel createCard(String title) {
    JLabel label = new JLabel("<html><center>" + title + "<br><h1>0</h1></center></html>");

    label.setOpaque(true);
    label.setBackground(Color.WHITE);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setBorder(new EmptyBorder(20, 20, 20, 20));

    return label;
  }

  private String cardText(String title, int value) {
    return "<html><center>" + title + "<br><h1>" + value + "</h1></center></html>";
  }

  public void setCheckedIn(int value) {
    checkedInLabel.setText(cardText("Checked In", value));
  }

  public void setReserved(int value) {
    reservedLabel.setText(cardText("Reservations", value));
  }

  public void setTodayCheckIn(int value) {
    checkedInLabel.setText(cardText("Today's Check-ins", value));
  }

  public void setTodayCheckOut(int value) {
    checkedInLabel.setText(cardText("Today's Check-outs", value));
  }
}
