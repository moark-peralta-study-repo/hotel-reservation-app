package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class LoginWrapper extends JPanel {
  private final JPanel leftPanel;
  private final JPanel rightPanel;
  private final JPanel bottomPanel;
  private final JPanel mainPanel;

  private final RoundedTextField usernameField = new RoundedTextField();
  private final RoundedPasswordField passwordField = new RoundedPasswordField();
  private final ActionButton loginBtn = new ActionButton("Login");
  private final ActionButton exitBtn = new ActionButton("Exit");

  public LoginWrapper() {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#f9fafb"));

    usernameField.setText("admin001");
    passwordField.setText("admin123");

    leftPanel = new JPanel();
    rightPanel = new JPanel();
    bottomPanel = new JPanel();
    mainPanel = new JPanel(new GridBagLayout());

    leftPanel.setPreferredSize(new Dimension(100, 0));
    rightPanel.setPreferredSize(new Dimension(100, 0));
    bottomPanel.setPreferredSize(new Dimension(0, 100));

    Color bg = Color.decode("#f9fafb");
    leftPanel.setBackground(bg);
    rightPanel.setBackground(bg);
    bottomPanel.setBackground(bg);
    mainPanel.setBackground(bg);

    LogoWrapper logo = new LogoWrapper();

    JPanel form = new JPanel();
    form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
    form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    form.setBackground(bg);

    form.add(new HeaderLabel("Welcome back"));
    form.add(Box.createVerticalStrut(15));

    form.add(new InputLabel("Username:"));
    form.add(Box.createVerticalStrut(5));
    form.add(usernameField);

    form.add(Box.createVerticalStrut(10));

    form.add(new InputLabel("Password:"));
    form.add(Box.createVerticalStrut(5));
    form.add(passwordField);

    form.add(Box.createVerticalStrut(15));

    JPanel btnPanel = new JPanel();
    btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
    btnPanel.setBackground(bg);

    btnPanel.add(loginBtn);
    btnPanel.add(Box.createHorizontalStrut(15));
    btnPanel.add(exitBtn);

    form.add(btnPanel);

    mainPanel.add(form);

    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.EAST);
    add(logo, BorderLayout.NORTH);
    add(mainPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  public String getUsername() {
    return usernameField.getText().trim();
  }

  public char[] getPassword() {
    return passwordField.getPassword();
  }

  public ActionButton getLoginBtn() {
    return loginBtn;
  }

  public ActionButton getExitBtn() {
    return exitBtn;
  }

  public RoundedTextField getUsernameField() {
    return usernameField;
  }

  public RoundedPasswordField getPasswordField() {
    return passwordField;
  }
}
