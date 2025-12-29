package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

  private JPanel navPanel;
  private JPanel contentPanel;
  private CardLayout cardLayout;
  private JButton roomsBtn;
  private JButton bookingsBtn;
  private JButton reservationBtn;
  private JButton checkInBtn;
  private JButton checkOutBtn;

  public JButton getReservationBtn() {
    return reservationBtn;
  }

  public JButton getCheckInBtn() {
    return checkInBtn;
  }

  public JButton getCheckOutBtn() {
    return checkOutBtn;
  }

  public JButton getBookingsBtn() {
    return bookingsBtn;
  }

  public JButton getRoomsBtn() {
    return roomsBtn;
  }

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
    roomsBtn = new NavButton("Rooms");
    // JButton bookingsBtn = new NavButton("Bookings");
    bookingsBtn = new NavButton("Bookings");
    // JButton reservationBtn = new NavButton("Reservations");
    reservationBtn = new NavButton("Reservations");
    // JButton checkInBtn = new NavButton("Check-in");
    checkInBtn = new NavButton("Check-ins");
    // JButton checkOutBtn = new NavButton("Check-out");
    checkOutBtn = new NavButton("Check-outs");

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

    contentPanel.add(new JPanel(), "Rooms");

    add(contentWrapper, BorderLayout.CENTER);

    setVisible(true);
  }
}
