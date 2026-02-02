package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LogoWrapper extends JPanel {
  public LogoWrapper() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    setBackground(Color.decode("#ffffff"));

    URL url = getClass().getClassLoader().getResource("icons/logo.png");
    System.out.println(url);

    ImageIcon logo = new ImageIcon(url);

    Image img = logo.getImage().getScaledInstance(200, 220, Image.SCALE_SMOOTH);
    logo = new ImageIcon(img);

    JLabel imageLbl = new JLabel(logo);
    add(imageLbl, BorderLayout.CENTER);

    setPreferredSize(new Dimension(200, 150));
  }
}
