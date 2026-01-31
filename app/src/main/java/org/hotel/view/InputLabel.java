package org.hotel.view;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class InputLabel extends JLabel {
  public InputLabel(String label) {
    super(label);
    setFont(new Font("Roboto", Font.BOLD, 16));
    setAlignmentX(JComponent.CENTER_ALIGNMENT);
  }
}
