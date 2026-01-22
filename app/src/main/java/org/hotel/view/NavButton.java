package org.hotel.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class NavButton extends JButton {

  private boolean active = false;

  private final Color defaultBg = Color.decode("#ffffff");
  private final Color activeBg = Color.decode("#e5e7eb");
  private final Color hoverBg = Color.decode("#f9fafb");

  private final Color defaultFg = Color.decode("#1f2937");
  private final Color activeFg = Color.decode("#4338ca"); // <— active color

  private FlatSVGIcon svgIcon;

  public NavButton(String text) {
    super(text);

    setBorderPainted(false);
    setFocusPainted(false);
    setOpaque(true);
    setBackground(defaultBg);
    setForeground(defaultFg);
    setMargin(new Insets(6, 15, 6, 6));
    setFont(UIManager.getFont("Button.font").deriveFont(Font.BOLD, 24f));
    setHorizontalAlignment(SwingConstants.LEFT);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        if (!active)
          setBackground(hoverBg);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (!active)
          setBackground(defaultBg);
      }
    });
  }

  public void setSvgIcon(String iconPath, int w, int h) {
    // give this button its OWN icon instance
    this.svgIcon = new FlatSVGIcon(iconPath, w, h);
    applyIconColor(); // apply current state color
    setIcon(svgIcon);
  }

  public void setActive(boolean active) {
    this.active = active;
    setBackground(active ? activeBg : defaultBg);
    setForeground(active ? activeFg : defaultFg);

    applyIconColor(); // recolor icon
    repaint();
  }

  private void applyIconColor() {
    if (svgIcon == null)
      return;

    Color target = active ? activeFg : defaultFg;

    svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> target));

    // Ensure Swing refreshes the icon visuals
    setIcon(svgIcon);
  }

  public boolean isActive() {
    return active;
  }
}
