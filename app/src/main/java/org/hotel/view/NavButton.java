package org.hotel.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.UIManager;

public class NavButton extends JButton {

  private boolean active = false;
  private Color defaultBg = Color.decode("#ffffff");
  private Color activeBg = Color.decode("#e5e7eb");
  private Color hoverBg = Color.decode("#f9fafb");

  public NavButton(String text) {
    super(text);

    setFont(UIManager.getFont("Button.font"));

    setBorderPainted(false);
    setFocusPainted(false);
    setOpaque(true);
    setBackground(Color.decode("#ffffff"));
    setForeground(Color.decode("#1f2937"));
    // setFont(new Font("MonoLisa", 1, 32));
    setFont(UIManager.getFont("Button.font").deriveFont(Font.BOLD, 24f));
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        // setBackground(Color.decode("#f9fafb"));
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

  public void setActive(boolean active) {
    this.active = active;
    setBackground(active ? activeBg : defaultBg);
    repaint();
  }

  public boolean isActive() {
    return active;
  }
}
