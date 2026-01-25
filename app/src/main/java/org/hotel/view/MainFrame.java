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
  private NavButton userBtn;

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

  public JButton getUsersBtn() {
    return userBtn;
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
        dashboardBtn, userBtn }) {
      btn.setActive(btn == clicked);
    }
  }

  private JPanel createNavButton(NavButton btn, String iconPath) {
    btn.setSvgIcon(iconPath, 24, 24);
    btn.setIconTextGap(12);

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(new EmptyBorder(5, 12, 5, 12));
    wrapper.setBackground(Color.decode("#ffffff"));
    wrapper.add(btn, BorderLayout.CENTER);

    return wrapper;
  }

  public void showView(String viewName) {
    cardLayout.show(contentPanel, viewName);
  }

  public MainFrame() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) screenSize.getWidth();
    int height = (int) screenSize.getHeight();
    int navWidth = width / 7;

    dashboardBtn = new NavButton("Dashboard");
    userBtn = new NavButton("Users");
    roomsBtn = new NavButton("Rooms");
    bookingsBtn = new NavButton("Bookings");
    reservationBtn = new NavButton("Reservations");
    checkInBtn = new NavButton("Check-ins");
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

    userBtn.addActionListener(e -> {
      setActiveBtn(userBtn);
    });

    setTitle("Lodgify");
    setSize(width, height);
    setBackground(Color.decode("#f9fafb"));
    setLocationRelativeTo(null);

    navPanel = new JPanel();
    navPanel.setLayout(new GridLayout(15, 1, 5, 5));
    navPanel.setBackground(Color.decode("#ffffff"));
    navPanel.setPreferredSize(new Dimension(navWidth, height));
    navPanel.setBackground(Color.decode("#ffffff"));
    navPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#f3f4f6")));

    add(navPanel, BorderLayout.WEST);

    navPanel.add(createNavButton(dashboardBtn,
        "icons/home.svg"));
    navPanel.add(createNavButton(bookingsBtn, "icons/calendar.svg"));
    navPanel.add(createNavButton(reservationBtn, "icons/user.svg"));
    navPanel.add(createNavButton(checkInBtn, "icons/in.svg"));
    navPanel.add(createNavButton(checkOutBtn, "icons/out.svg"));

    navPanel.add(new BottomDivider());

    navPanel.add(createNavButton(roomsBtn, "icons/bed.svg"));
    navPanel.add(createNavButton(userBtn, "icons/user.svg"));

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
