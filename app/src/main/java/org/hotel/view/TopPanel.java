package org.hotel.view;

import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel {
  public TopPanel(String username) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#ffffff"));
    // setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    setPreferredSize(new Dimension(0, 80));
    setBorder(BorderFactory.createLineBorder(Color.decode("#f3f4f6")));

    JLabel nameLabel = new JLabel("Welcome, " + username);
    nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
    nameLabel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

    JButton logoutButton = new NavButton("Logout");
    logoutButton.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
    logoutButton.setFont(new Font("MonoLisa", 1, 16));

    add(nameLabel, BorderLayout.WEST);
    add(logoutButton, BorderLayout.EAST);
  }
}
