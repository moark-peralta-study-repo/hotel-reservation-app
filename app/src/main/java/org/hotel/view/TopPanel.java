package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TopPanel extends JPanel {

  private final IconButton logoutButton;
  private final JLabel nameLabel;

  public TopPanel(String username) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#ffffff"));
    setPreferredSize(new Dimension(0, 64));
    setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#f3f4f6")));

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    left.setOpaque(false);
    left.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

    nameLabel = new JLabel("Welcome, " + username);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
    nameLabel.setForeground(Color.decode("#111827"));
    left.add(nameLabel);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    right.setOpaque(false);
    right.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

    logoutButton = new IconButton("icons/logout.svg", 24);

    right.add(logoutButton);

    add(left, BorderLayout.WEST);
    add(right, BorderLayout.EAST);
  }

  public JButton getLogoutButton() {
    return logoutButton;
  }

  public void setUsername(String username) {
    nameLabel.setText("Welcome, " + username);
  }
}
