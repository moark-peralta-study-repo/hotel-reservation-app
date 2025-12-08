package org.hotel.view;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
  private JPanel navPanel;
  private JPanel contentPanel;
  private CardLayout cardLayout;

  public JPanel getNavPanel() {
    return navPanel;
  }

  public JPanel getContentPanel() {
    return contentPanel;
  }

  public CardLayout getCardLayout() {
    return cardLayout;
  }

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

    roomsBtn.addActionListener(e -> cardLayout.show(contentPanel, "Rooms"));

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
    navPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#f3f4f6")));

    add(navPanel, BorderLayout.WEST);

    navPanel.add(dashBoardBtn);
    navPanel.add(roomsBtn);
    navPanel.add(bookingsBtn);
    navPanel.add(reservationBtn);
    navPanel.add(checkInBtn);
    navPanel.add(checkOutBtn);

    JPanel contentWrapper = new JPanel(new BorderLayout());

    TopPanel topPanel = new TopPanel("Mark Lester Peralta");
    contentWrapper.add(topPanel, BorderLayout.NORTH);

    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);
    contentWrapper.add(contentPanel, BorderLayout.CENTER);

    add(contentWrapper, BorderLayout.CENTER);

    setVisible(true);
  }
}
