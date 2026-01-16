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
import javax.swing.border.EmptyBorder;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class MainFrame extends JFrame {

  private JPanel navPanel;
  private JPanel contentPanel;
  private CardLayout cardLayout;
  private NavButton roomsBtn;
  private NavButton bookingsBtn;
  private NavButton reservationBtn;
  private NavButton checkInBtn;
  private NavButton checkOutBtn;
  private NavButton dashboardBtn;

  public JButton getReservationBtn() {
    return reservationBtn;
  }

  public JButton getDashboardBtn() {
    return dashboardBtn;
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

  private void setActiveBtn(NavButton clicked) {
    for (NavButton btn : new NavButton[] { roomsBtn, bookingsBtn, reservationBtn, checkInBtn, checkOutBtn,
        dashboardBtn }) {
      btn.setActive(btn == clicked);
    }
  }

  private JPanel createNavButton(NavButton btn, String iconPath) {
    FlatSVGIcon icon = new FlatSVGIcon(iconPath, 24, 24);
    btn.setIcon(icon);
    btn.setIconTextGap(12);

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(new EmptyBorder(5, 12, 5, 12));
    wrapper.setBackground(Color.decode("#ffffff"));
    wrapper.add(btn, BorderLayout.CENTER);

    return wrapper;
  }

  // private JPanel createNavButton(NavButton btn) {
  // JPanel wrapper = new JPanel(new BorderLayout());
  // wrapper.setBorder(new EmptyBorder(5, 12, 5, 12));
  // wrapper.setBackground(Color.decode("#ffffff"));
  // wrapper.add(btn, BorderLayout.CENTER);
  //
  // return wrapper;
  // }

  public void showView(String viewName) {
    cardLayout.show(contentPanel, viewName);
  }

  public MainFrame() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) screenSize.getWidth();
    int height = (int) screenSize.getHeight();
    int navWidth = width / 7;

    // Buttons
    dashboardBtn = new NavButton("Dashboard");
    roomsBtn = new NavButton("Rooms");
    // JButton bookingsBtn = new NavButton("Bookings");
    bookingsBtn = new NavButton("Bookings");
    // JButton reservationBtn = new NavButton("Reservations");
    reservationBtn = new NavButton("Reservations");
    // JButton checkInBtn = new NavButton("Check-in");
    checkInBtn = new NavButton("Check-ins");
    // JButton checkOutBtn = new NavButton("Check-out");
    checkOutBtn = new NavButton("Check-outs");

    dashboardBtn.addActionListener(e -> {
      setActiveBtn(dashboardBtn);
    });

    roomsBtn.addActionListener(e -> {
      setActiveBtn(roomsBtn);
    });

    checkInBtn.addActionListener(e -> {
      setActiveBtn(checkInBtn);
    });

    checkOutBtn.addActionListener(e -> {
      setActiveBtn(checkOutBtn);
    });

    reservationBtn.addActionListener(e -> {
      setActiveBtn(reservationBtn);
    });

    bookingsBtn.addActionListener(e -> {
      setActiveBtn(bookingsBtn);
    });

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

    navPanel.add(createNavButton(dashboardBtn,
        "../../../../../main/resources/bed-svgrepo-com.svg"));

    // navPanel.add(createNavButton(dashboardBtn));
    // navPanel.add(createNavButton(roomsBtn));
    // navPanel.add(createNavButton(bookingsBtn));
    // navPanel.add(createNavButton(checkInBtn));
    // navPanel.add(createNavButton(checkOutBtn));

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
