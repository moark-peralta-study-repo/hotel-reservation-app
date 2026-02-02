package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.hotel.model.User;
import org.hotel.model.UserRole;

public class MainFrame extends JFrame {

  private JPanel navPanel;
  private JPanel contentPanel;
  private CardLayout cardLayout;

  private TopPanel topPanel;

  private NavButton roomsBtn;
  private NavButton bookingsBtn;
  private NavButton reservationBtn;
  private NavButton checkInBtn;
  private NavButton checkOutBtn;
  private NavButton dashboardBtn;
  private NavButton userBtn;

  // Track only buttons that exist for the current role
  private final List<NavButton> visibleNavButtons = new ArrayList<>();

  private final User loggedInUser;

  public MainFrame(User user) {
    this.loggedInUser = user;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) screenSize.getWidth();
    int height = (int) screenSize.getHeight();
    int navWidth = width / 7;

    setTitle("Lodgify");
    setSize(width, height);
    setBackground(Color.decode("#f9fafb"));
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel sidePanel = new JPanel(new BorderLayout());
    sidePanel.setPreferredSize(new Dimension(navWidth, height));
    sidePanel.setBackground(Color.decode("#ffffff"));
    sidePanel.setBorder(BorderFactory.createLineBorder(Color.decode("#f3f4f6")));
    add(sidePanel, BorderLayout.WEST);

    JPanel logoPanel = new JPanel(new BorderLayout());
    logoPanel.setPreferredSize(new Dimension(navWidth, 150));
    logoPanel.setBackground(Color.decode("#ffffff"));
    logoPanel.add(new LogoWrapper(), BorderLayout.CENTER);
    sidePanel.add(logoPanel, BorderLayout.NORTH);

    navPanel = new JPanel(new GridLayout(15, 1, 5, 5));
    navPanel.setBackground(Color.decode("#ffffff"));
    navPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
    sidePanel.add(navPanel, BorderLayout.CENTER);

    dashboardBtn = new NavButton("Dashboard");
    bookingsBtn = new NavButton("Bookings");
    reservationBtn = new NavButton("Reservations");
    checkInBtn = new NavButton("Check-ins");
    checkOutBtn = new NavButton("Check-outs");

    registerNavButton(dashboardBtn);
    registerNavButton(bookingsBtn);
    registerNavButton(reservationBtn);
    registerNavButton(checkInBtn);
    registerNavButton(checkOutBtn);

    navPanel.add(createNavButton(dashboardBtn, "icons/home.svg"));
    navPanel.add(createNavButton(bookingsBtn, "icons/calendar.svg"));
    navPanel.add(createNavButton(reservationBtn, "icons/user.svg"));
    navPanel.add(createNavButton(checkInBtn, "icons/in.svg"));
    navPanel.add(createNavButton(checkOutBtn, "icons/out.svg"));

    if (loggedInUser.getRole() == UserRole.ADMIN) {
      navPanel.add(new BottomDivider());

      roomsBtn = new NavButton("Rooms");
      userBtn = new NavButton("Users");

      registerNavButton(roomsBtn);
      registerNavButton(userBtn);

      navPanel.add(createNavButton(roomsBtn, "icons/bed.svg"));
      navPanel.add(createNavButton(userBtn, "icons/user.svg"));
    }

    JPanel contentWrapper = new JPanel(new BorderLayout());

    topPanel = new TopPanel(user.getUsername());
    contentWrapper.add(topPanel, BorderLayout.NORTH);

    topPanel.getLogoutButton().addActionListener(e -> {
      int confirm = JOptionPane.showConfirmDialog(
          this,
          "Are you sure you want to logout?",
          "Logout",
          JOptionPane.YES_NO_OPTION);

      if (confirm == JOptionPane.YES_OPTION) {
        dispose();
        new LoginFrame();
      }
    });

    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);
    contentWrapper.add(contentPanel, BorderLayout.CENTER);

    add(contentWrapper, BorderLayout.CENTER);

    setVisible(true);
  }

  private void registerNavButton(NavButton btn) {
    visibleNavButtons.add(btn);
    btn.addActionListener(e -> setActiveBtn(btn));
  }

  private void setActiveBtn(NavButton clicked) {
    for (NavButton btn : visibleNavButtons) {
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
    if (loggedInUser.getRole() == UserRole.RECEPTIONIST) {
      if ("rooms".equalsIgnoreCase(viewName) || "users".equalsIgnoreCase(viewName)) {
        JOptionPane.showMessageDialog(
            this,
            "Access denied.",
            "Permission",
            JOptionPane.WARNING_MESSAGE);
        return;
      }
    }
    cardLayout.show(contentPanel, viewName);
  }

  public JButton getReservationBtn() {
    return reservationBtn;
  }

  public JButton getDashboardBtn() {
    return dashboardBtn;
  }

  public JButton getCheckInBtn() {
    return checkInBtn;
  }

    navPanel.add(new LogoWrapper());
    navPanel.add(createNavButton(dashboardBtn,
        "icons/home.svg"));
    navPanel.add(createNavButton(bookingsBtn, "icons/calendar.svg"));
    navPanel.add(createNavButton(reservationBtn, "icons/user.svg"));
    navPanel.add(createNavButton(checkInBtn, "icons/in.svg"));
    navPanel.add(createNavButton(checkOutBtn, "icons/out.svg"));

  public JButton getBookingsBtn() {
    return bookingsBtn;
  }

  public JButton getRoomsBtn() {
    return roomsBtn;
  } // may be null

  public JButton getUsersBtn() {
    return userBtn;
  } // may be null

  public JPanel getNavPanel() {
    return navPanel;
  }

  public JPanel getContentPanel() {
    return contentPanel;
  }

  public CardLayout getCardLayout() {
    return cardLayout;
  }

  public User getLoggedInUser() {
    return loggedInUser;
  }
}
