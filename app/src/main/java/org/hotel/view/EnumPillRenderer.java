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

public abstract class EnumPillRenderer<T extends Enum<T>>
    extends JLabel implements TableCellRenderer {

  public EnumPillRenderer() {
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

    if (value != null && value.getClass().isEnum()) {
      @SuppressWarnings("unchecked")
      T enumValue = (T) value;

      setText(format(enumValue));
      setForeground(getTextColor(enumValue));
      setBackground(getBgColor(enumValue));
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

  protected String format(T value) {
    String s = value.name().toLowerCase().replace('_', ' ');
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  protected abstract Color getBgColor(T value);

  protected abstract Color getTextColor(T value);
}
