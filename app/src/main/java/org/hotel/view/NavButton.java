package org.hotel.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NavButton extends JButton {

  public NavButton(String text) {
    super(text);

    setBorderPainted(false);
    setFocusPainted(false);
    setContentAreaFilled(false);
    setOpaque(true);
    setBackground(Color.decode("#ffffff"));
    setForeground(Color.decode("#1f2937"));
    setFont(new Font("MonoLisa", 1, 32));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(Color.decode("#f9fafb"));
      }

      public void mouseExited(MouseEvent e) {
        setBackground(Color.decode("#ffffff"));
      }
    });
  }
}
