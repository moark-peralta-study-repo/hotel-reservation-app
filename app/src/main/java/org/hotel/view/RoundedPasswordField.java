package org.hotel.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPasswordField;

public class RoundedPasswordField extends JPasswordField {
  private final int radius = 12;
  private final Color focusBorderColor = Color.decode("#7c86ff");
  private Color borderColor = Color.decode("#4338ca");

  public RoundedPasswordField() {
    setOpaque(false);
    setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    setFont(new Font("Poppins", Font.PLAIN, 16));

    setPreferredSize(new Dimension(300, 36));
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    setAlignmentX(JComponent.CENTER_ALIGNMENT);

    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        repaint();
      }

      @Override
      public void focusLost(FocusEvent e) {
        repaint();
      }
    });
  }

  public void setError(boolean error) {
    borderColor = error ? Color.RED : Color.decode("#4338ca");
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(Color.WHITE);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

    g2.dispose();
    super.paintComponent(g);
  }

  @Override
  protected void paintBorder(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(hasFocus() ? focusBorderColor : borderColor);
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

    g2.dispose();
  }
}
