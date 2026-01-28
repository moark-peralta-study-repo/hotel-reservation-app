package org.hotel.controller;

import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.hotel.model.User;
import org.hotel.model.UserRole;
import org.hotel.model.dao.UsersDAO;
import org.hotel.view.MainFrame;
import org.hotel.view.RoundedPasswordField;
import org.hotel.view.RoundedTextField;
import org.hotel.view.UsersView;

public class UserController {
  private MainFrame mainFrame;
  private UsersDAO usersDAO;
  private UsersView usersView;

  public UserController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.usersDAO = new UsersDAO();

    registerEvents();
  }

  private void registerEvents() {
    mainFrame.getUsersBtn().addActionListener(e -> loadUsers());
  }

  private void onAddUser() {
    User user = showAddDialog();

    if (user != null) {
      usersDAO.insert(user);
      loadUsers();
    }
  }

  private void showEditDialog() {
    RoundedTextField fnameField = new RoundedTextField();
    RoundedTextField lnameField = new RoundedTextField();
    RoundedTextField usernameField = new RoundedTextField();
    RoundedPasswordField passwordField = new RoundedPasswordField();
    RoundedPasswordField confirmPasswordField = new RoundedPasswordField();
    JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());

  }

  private User showAddDialog() {
    RoundedTextField fnameField = new RoundedTextField();
    RoundedTextField lnameField = new RoundedTextField();
    RoundedTextField usernameField = new RoundedTextField();
    RoundedPasswordField passwordField = new RoundedPasswordField();
    RoundedPasswordField confirmPasswordField = new RoundedPasswordField();
    JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());

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

    addUserPanel.add(new JLabel("Role: "));
    addUserPanel.add(roleCombo);

    int result = JOptionPane.showConfirmDialog(
        mainFrame,
        addUserPanel,
        "Add User",
        JOptionPane.OK_CANCEL_OPTION);

    if (result != JOptionPane.OK_OPTION) {
      // wipe typed passwords before leaving
      Arrays.fill(passwordField.getPassword(), '\0');
      Arrays.fill(confirmPasswordField.getPassword(), '\0');
      return null;
    }

    char[] pw = passwordField.getPassword();
    char[] confirm = confirmPasswordField.getPassword();

    if (!Arrays.equals(pw, confirm)) {
      Arrays.fill(pw, '\0');
      Arrays.fill(confirm, '\0');
      JOptionPane.showMessageDialog(mainFrame, "Passwords do not match.");
      return null;
    }

    UserRole role = (UserRole) roleCombo.getSelectedItem();

    return new User(
        0,
        fnameField.getText().trim(),
        lnameField.getText().trim(),
        usernameField.getText().trim(),
        pw,
        role);
  }

  private void loadUsers() {
    List<User> users = usersDAO.getAll();

    usersView = new UsersView(users);
    usersView.getAddUserBtn().addActionListener(e -> onAddUser());

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(usersView, "Users");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Users");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }
}
