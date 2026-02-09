package org.hotel.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class RoomStatusPillRenderer extends JLabel implements TableCellRenderer {

  public RoomStatusPillRenderer() {
    setOpaque(false);
    setHorizontalAlignment(CENTER);
    setFont(new Font("Poppins", Font.BOLD, 14));
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table,
      Object value,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column) {

    String text = "";

    // supports: "Available"/"Occupied" strings OR boolean true/false
    if (value instanceof String s) {
      text = s;
    } else if (value instanceof Boolean b) {
      text = b ? "Available" : "Occupied";
    }

    setText(text);

    boolean available = "available".equalsIgnoreCase(text.trim());

    setBackground(available ? Color.decode("#C8E6C9") : Color.decode("#FFCDD2")); // green / red
    setForeground(available ? Color.decode("#2E7D32") : Color.decode("#C62828"));

    return this;
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int arc = getHeight();
    g2.setColor(getBackground());
    g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, arc, arc);

    g2.dispose();
    super.paintComponent(g);
  }
}
