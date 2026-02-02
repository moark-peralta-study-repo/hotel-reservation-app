
package org.hotel.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class IconButton extends JButton {

  private boolean hovering = false;

  private final Color hoverBg = Color.decode("#f3f4f6");
  private final Color pressBg = Color.decode("#e5e7eb");
  private final Color foreground = Color.decode("#464fe5");

  public IconButton(String svgPath, int iconSize) {
    FlatSVGIcon icon = (new FlatSVGIcon(svgPath, iconSize, iconSize));

    icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> foreground));
    setIcon(icon);

    setText(null);
    setHorizontalAlignment(CENTER);
    setVerticalAlignment(CENTER);
    setForeground(foreground);

    setFocusable(false);
    setBorderPainted(false);
    setContentAreaFilled(false);
    setOpaque(false);

    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setPreferredSize(new Dimension(50, 50));
    setToolTipText("Logout");

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

    if (getModel().isPressed()) {
      g2.setColor(pressBg);
      g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 16, 16);
    } else if (hovering) {
      g2.setColor(hoverBg);
      g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 16, 16);
    }

    g2.dispose();
    super.paintComponent(g);
  }
}
