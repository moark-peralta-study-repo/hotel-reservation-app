
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

import org.hotel.model.BookingStatus;

public class StatusPillRenderer extends JLabel implements TableCellRenderer {

  public StatusPillRenderer() {
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

    if (value instanceof BookingStatus status) {
      setText(format(status));
      setForeground(getTextColor(status));
      setBackground(getBgColor(status));
    } else {
      setText("");
    }

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

  private String format(BookingStatus status) {
    String s = status.name().toLowerCase().replace('_', ' ');
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  private Color getBgColor(BookingStatus status) {
    return switch (status) {
      case RESERVED -> Color.decode("#BBDEFB");
      case CHECKED_IN -> Color.decode("#C8E6C9");
      case CHECKED_OUT -> Color.decode("#EEEEEE");
      case CANCELLED -> Color.decode("#FFB8B8");
    };
  }

  private Color getTextColor(BookingStatus status) {
    return switch (status) {
      case RESERVED -> Color.decode("#1565C0");
      case CHECKED_IN -> Color.decode("#2E7D32");
      case CHECKED_OUT -> Color.decode("#616161");
      case CANCELLED -> Color.decode("#C62828");
    };
  }
}
