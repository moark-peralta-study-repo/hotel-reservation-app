package org.hotel.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class BottomDivider extends JComponent {

  private final Color lineColor = Color.decode("#e5e7eb");
  private final int inset = 12;
  private final float thickness = 2.5f;

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(0, 12);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(lineColor);
    g2.setStroke(new BasicStroke(
        thickness,
        BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND));

    int y = getHeight() - Math.round(thickness);
    g2.drawLine(inset, y, getWidth() - inset, y);

    g2.dispose();
  }
}
