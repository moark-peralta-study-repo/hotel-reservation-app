package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

public class ShowAddUserDialog extends JDialog {

  private RoundedTextField fnameField, lnameField, usernameField, passwordField, confirmPasswordField;

  public ShowAddUserDialog(JFrame parent) {
    super(parent, "Add User", true); // modal dialog

    fnameField = new RoundedTextField();
    lnameField = new RoundedTextField();
    usernameField = new RoundedTextField();
    passwordField = new RoundedTextField();
    confirmPasswordField = new RoundedTextField();

    JPanel addUserPanel = new JPanel(new GridLayout(0, 1, 8, 8));
    addUserPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    addUserPanel.add(new JLabel("First Name: "));
    addUserPanel.add(fnameField);

    addUserPanel.add(new JLabel("Last Name: "));
    addUserPanel.add(lnameField);

    addUserPanel.add(new JLabel("Username: "));
    addUserPanel.add(usernameField);

    addUserPanel.add(new JLabel("Password: "));
    addUserPanel.add(passwordField);

    addUserPanel.add(new JLabel("Confirm Password: "));
    addUserPanel.add(confirmPasswordField);

    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(e -> dispose());

    JPanel bottom = new JPanel();
    bottom.add(closeBtn);

    setLayout(new BorderLayout());
    add(addUserPanel, BorderLayout.CENTER);
    add(bottom, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }
}
