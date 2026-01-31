package org.hotel.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class HeaderLabel extends JLabel {
  public HeaderLabel(String text) {
    super(text);
    setFont(new Font("Roboto", Font.BOLD, 22));
    setForeground(Color.decode("#111827"));
    setAlignmentX(JComponent.CENTER_ALIGNMENT);
  }
}
