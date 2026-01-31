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

  private void onDeleteUser() {
    int selectedRow = usersView.getUsersTable().getSelectedRow();

    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(mainFrame, "Please select a user to delete");
      return;
    }

    int id = (int) usersView.getTableModel().getValueAt(selectedRow, 1);
    User user = usersDAO.getById(id);

    int confirm = JOptionPane.showConfirmDialog(
        null,
        "Are you sure you want to delete User " + user.getId() + "?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      usersDAO.delete(user.getId());
      loadUsers();
    }
  }

  private void onAddUser() {
    User user = showAddDialog();

    if (user != null) {
      usersDAO.insert(user);
      loadUsers();
    }
  }

  private User showEditDialog(User user) {
    RoundedTextField fnameField = new RoundedTextField(user.getFirstName());
    RoundedTextField lnameField = new RoundedTextField(user.getLastName());
    RoundedTextField usernameField = new RoundedTextField(user.getUsername());

    RoundedPasswordField passwordField = new RoundedPasswordField();
    RoundedPasswordField confirmPasswordField = new RoundedPasswordField();

    JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
    roleCombo.setSelectedItem(user.getRole());

    JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    panel.add(new JLabel("First Name: "));
    panel.add(fnameField);

    panel.add(new JLabel("Last Name: "));
    panel.add(lnameField);

    panel.add(new JLabel("New Password: "));
    panel.add(passwordField);

    panel.add(new JLabel("Confirm New Password: "));
    panel.add(confirmPasswordField);

    panel.add(new JLabel("Role: "));
    panel.add(roleCombo);

    int result = JOptionPane.showConfirmDialog(
        mainFrame,
        panel,
        "Edit User",
        JOptionPane.OK_CANCEL_OPTION);

    if (result != JOptionPane.OK_OPTION) {
      Arrays.fill(passwordField.getPassword(), '\0');
      Arrays.fill(confirmPasswordField.getPassword(), '\0');
      return null;
    }

    String firstName = fnameField.getText().trim();
    String lastName = lnameField.getText().trim();
    String username = usernameField.getText().trim();
    UserRole role = (UserRole) roleCombo.getSelectedItem();

    if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
      JOptionPane.showMessageDialog(mainFrame, "All fields except password are required.");
      Arrays.fill(passwordField.getPassword(), '\0');
      Arrays.fill(confirmPasswordField.getPassword(), '\0');
      return null;
    }
    char[] pw = passwordField.getPassword();
    char[] confirm = confirmPasswordField.getPassword();

    char[] finalPassword;

    if (pw.length == 0 && confirm.length == 0) {
      finalPassword = user.getPassword();
    } else {
      if (!Arrays.equals(pw, confirm)) {
        Arrays.fill(pw, '\0');
        Arrays.fill(confirm, '\0');
        JOptionPane.showMessageDialog(mainFrame, "Passwords do not match.");
        return null;
      }
      finalPassword = pw; // use new password
    }

    // Build updated user (keep same id)
    return new User(
        user.getId(),
        firstName,
        lastName,
        username,
        finalPassword,
        role);
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
    usersView.getDeleteUserBtn().addActionListener(e -> onDeleteUser());
    usersView.getEditUserBtn().addActionListener(e -> onEditUser());

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(usersView, "Users");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Users");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void onEditUser() {
    int selectedRow = usersView.getUsersTable().getSelectedRow();

    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(mainFrame, "Please select a user to edit");
      return;
    }

    int id = (int) usersView.getTableModel().getValueAt(selectedRow, 1);
    User existing = usersDAO.getById(id);

    if (existing == null) {
      JOptionPane.showMessageDialog(mainFrame, "User not found.");
      return;
    }

    User updated = showEditDialog(existing);

    if (updated != null) {
      usersDAO.update(updated);
      loadUsers();
    }
  }
}
