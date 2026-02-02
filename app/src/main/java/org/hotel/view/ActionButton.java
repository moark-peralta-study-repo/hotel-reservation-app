package org.hotel.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class ActionButton extends JButton {
  private final Color defaultBg = Color.decode("#464fe5");
  private final Color hoverBg = Color.decode("#4338ca");
  private boolean hovering = false;

  public ActionButton(String text) {
    super(text);

    setContentAreaFilled(false);
    setFocusPainted(false);
    setBorderPainted(false);
    setOpaque(false);

    setForeground(Color.WHITE);
    setFont(new Font("Poppins", Font.BOLD, 18));
    setMargin(new Insets(30, 40, 30, 40));
    setPreferredSize(new Dimension(80, 40));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        hovering = true;
        repaint();
      }

      @Override
      public void mouseExited(MouseEvent e) {
        hovering = false;
        repaint();
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(hovering ? hoverBg : defaultBg);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);

    g2.dispose();
    super.paintComponent(g);
  }
}
