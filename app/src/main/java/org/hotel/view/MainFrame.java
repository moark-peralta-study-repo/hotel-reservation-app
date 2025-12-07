package org.hotel.view;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
  private JPanel navPanel;
  private JPanel contentPanel;
  private CardLayout cardLayout;

  public MainFrame() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) screenSize.getWidth();
    int height = (int) screenSize.getHeight();
    int navWidth = width / 6;

    // Buttons
    JButton dashBoardBtn = new NavButton("Dashboard");
    JButton roomsBtn = new NavButton("Rooms");
    JButton bookingsBtn = new NavButton("Bookings");
    JButton reservationBtn = new NavButton("Reservations");
    JButton checkInBtn = new NavButton("Check-in");
    JButton checkOutBtn = new NavButton("Check-out");

    setTitle("Hotel Sugu");
    setSize(width, height);
    setBackground(Color.decode("#f9fafb"));
    // setBackground(Color.decode("#e5e7eb"));
    setLocationRelativeTo(null);

    navPanel = new JPanel();
    navPanel.setLayout(new GridLayout(10, 1, 5, 5));
    navPanel.setBackground(Color.decode("#ffffff"));
    navPanel.setPreferredSize(new Dimension(navWidth, height));
    navPanel.setBackground(Color.decode("#ffffff"));
    navPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#000000")));
    add(navPanel, BorderLayout.WEST);

    navPanel.add(dashBoardBtn);
    navPanel.add(roomsBtn);
    navPanel.add(bookingsBtn);
    navPanel.add(reservationBtn);
    navPanel.add(checkInBtn);
    navPanel.add(checkOutBtn);
    setVisible(true);
  }
}
