package org.hotel.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.hotel.model.User;
import org.hotel.model.UserRole;
import org.hotel.model.dao.UsersDAO;
import org.hotel.view.HeaderLabel;
import org.hotel.view.MainFrame;
import org.hotel.view.RoundedPasswordField;
import org.hotel.view.RoundedTextField;
import org.hotel.view.UsersView;

public class UserController {

  private final MainFrame mainFrame;
  private final UsersDAO usersDAO;
  private UsersView usersView;

  private static final int USERNAME_MIN = 4;
  private static final int USERNAME_MAX = 20;
  private static final int PASSWORD_MIN = 6;

  private static final String USERNAME_REGEX = "^[A-Za-z0-9_]+$";
  private static final Color BG = Color.decode("#f9fafb");
  private static final Color ERR = Color.decode("#ef4444");

  public UserController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.usersDAO = new UsersDAO();
    registerEvents();
  }

  private void registerEvents() {
    if (mainFrame.getUsersBtn() != null) {
      mainFrame.getUsersBtn().addActionListener(e -> loadUsers());
    }
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

  private void onAddUser() {
    UserFormDialog dialog = new UserFormDialog(mainFrame, "Add User", null);
    dialog.setVisible(true);

    User created = dialog.getResult();
    if (created != null) {
      usersDAO.insert(created);
      loadUsers();
    }
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

    UserFormDialog dialog = new UserFormDialog(mainFrame, "Edit User", existing);
    dialog.setVisible(true);

    User updated = dialog.getResult();
    if (updated != null) {
      usersDAO.update(updated);
      loadUsers();
    }
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
        mainFrame,
        "Are you sure you want to delete user " + (user != null ? user.getUsername() : id) + "?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      usersDAO.delete(id);
      loadUsers();
    }
  }

  private class UserFormDialog extends JDialog {

    private User result = null;

    private final RoundedTextField fnameField = new RoundedTextField();
    private final RoundedTextField lnameField = new RoundedTextField();
    private final RoundedTextField usernameField = new RoundedTextField();
    private final RoundedPasswordField passwordField = new RoundedPasswordField();
    private final RoundedPasswordField confirmPasswordField = new RoundedPasswordField();
    private final JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());

    private final JLabel fnameErr = errLabel();
    private final JLabel lnameErr = errLabel();
    private final JLabel userErr = errLabel();
    private final JLabel passErr = errLabel();
    private final JLabel confErr = errLabel();
    private final JLabel roleErr = errLabel();

    private final User existing;

    UserFormDialog(MainFrame parent, String title, User existing) {
      super(parent, title, true);
      this.existing = existing;

      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setLayout(new BorderLayout());
      getContentPane().setBackground(BG);

      setSize(520, 560);
      setMinimumSize(new Dimension(460, 420));
      setLocationRelativeTo(parent);

      if (existing != null) {
        fnameField.setText(existing.getFirstName());
        lnameField.setText(existing.getLastName());
        usernameField.setText(existing.getUsername());
        roleCombo.setSelectedItem(existing.getRole());
      }

      add(buildHeader(title), BorderLayout.NORTH);
      add(buildScrollableForm(), BorderLayout.CENTER);
      add(buildActions(), BorderLayout.SOUTH);

      addValidateOnBlur(fnameField);
      addValidateOnBlur(lnameField);
      addValidateOnBlur(usernameField);
      addValidateOnBlur(passwordField);
      addValidateOnBlur(confirmPasswordField);
      roleCombo.addActionListener(e -> validateForm(false));
    }

    User getResult() {
      return result;
    }

    private JPanel buildHeader(String titleText) {
      JPanel header = new JPanel(new BorderLayout());
      header.setBackground(BG);
      header.setBorder(BorderFactory.createEmptyBorder(18, 18, 10, 18));

      header.add(new HeaderLabel(titleText), BorderLayout.WEST);
      return header;
    }

    private JScrollPane buildScrollableForm() {
      JScrollPane scroll = new JScrollPane(buildForm());
      scroll.setBorder(null);
      scroll.getViewport().setBackground(BG);
      scroll.getVerticalScrollBar().setUnitIncrement(16);
      return scroll;
    }

    private JPanel buildForm() {
      JPanel form = new JPanel(new GridBagLayout());
      form.setBackground(BG);
      form.setBorder(BorderFactory.createEmptyBorder(12, 18, 18, 18));

      GridBagConstraints g = new GridBagConstraints();
      g.gridx = 0;
      g.gridy = 0;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.weightx = 1;
      g.insets = new Insets(6, 0, 2, 0);

      addRow(form, g, "First Name *", fnameField, fnameErr);
      addRow(form, g, "Last Name *", lnameField, lnameErr);
      addRow(form, g, "Username *", usernameField, userErr);

      addRow(form, g, existing == null ? "Password *" : "New Password (optional)", passwordField, passErr);
      addRow(form, g, existing == null ? "Confirm Password *" : "Confirm New Password", confirmPasswordField, confErr);

      addRow(form, g, "Role *", roleCombo, roleErr);

      if (existing != null) {
        JLabel hint = new JLabel("Leave password blank to keep current password.");
        hint.setForeground(Color.decode("#6b7280"));
        hint.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 2));
        g.gridy++;
        form.add(hint, g);
      }

      return form;
    }

    private void addRow(JPanel form, GridBagConstraints g, String label, JComponent field, JLabel err) {
      JLabel lbl = new JLabel(label);
      lbl.setForeground(Color.decode("#374151"));
      lbl.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

      form.add(lbl, g);
      g.gridy++;

      enforceFieldSize(field);
      form.add(field, g);
      g.gridy++;

      form.add(err, g);
      g.gridy++;

      g.insets = new Insets(10, 0, 2, 0);
    }

    private JPanel buildActions() {
      JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
      actions.setBackground(BG);
      actions.setBorder(BorderFactory.createEmptyBorder(10, 18, 14, 18));

      JButton cancelBtn = new JButton("Cancel");
      JButton saveBtn = new JButton(existing == null ? "Create" : "Save");

      cancelBtn.addActionListener(e -> {
        result = null;
        wipe(passwordField.getPassword());
        wipe(confirmPasswordField.getPassword());
        dispose();
      });

      saveBtn.addActionListener(e -> onSubmit());

      actions.add(cancelBtn);
      actions.add(saveBtn);
      return actions;
    }

    private void onSubmit() {
      if (!validateForm(true)) {
        result = null; // HARD BLOCK
        return;
      }

      String first = fnameField.getText().trim();
      String last = lnameField.getText().trim();
      String uname = usernameField.getText().trim();
      UserRole role = (UserRole) roleCombo.getSelectedItem();

      char[] pw = passwordField.getPassword();
      char[] confirm = confirmPasswordField.getPassword();

      char[] finalPw;

      if (existing == null) {

        finalPw = pw;
        wipe(confirm);
        result = new User(0, first, last, uname, finalPw, role);
      } else {

        if (pw.length == 0 && confirm.length == 0) {
          finalPw = existing.getPassword();
        } else {
          finalPw = pw;
          wipe(confirm);
        }

        result = new User(existing.getId(), first, last, uname, finalPw, role);
      }

      dispose();
    }

    private boolean validateForm(boolean focusFirstBad) {
      clearErrors();
      boolean ok = true;

      JComponent firstBad = null;

      String first = fnameField.getText().trim();
      String last = lnameField.getText().trim();
      String uname = usernameField.getText().trim();
      UserRole role = (UserRole) roleCombo.getSelectedItem();

      char[] pw = passwordField.getPassword();
      char[] cf = confirmPasswordField.getPassword();

      if (first.isEmpty()) {
        setErr(fnameErr, "First name is required.");
        ok = false;
        if (firstBad == null)
          firstBad = fnameField;
      }

      if (last.isEmpty()) {
        setErr(lnameErr, "Last name is required.");
        ok = false;
        if (firstBad == null)
          firstBad = lnameField;
      }

      if (uname.isEmpty()) {
        setErr(userErr, "Username is required.");
        ok = false;
        if (firstBad == null)
          firstBad = usernameField;
      } else {
        if (uname.length() < USERNAME_MIN || uname.length() > USERNAME_MAX) {
          setErr(userErr, "Username must be " + USERNAME_MIN + "–" + USERNAME_MAX + " characters.");
          ok = false;
          if (firstBad == null)
            firstBad = usernameField;
        } else if (!uname.matches(USERNAME_REGEX)) {
          setErr(userErr, "Only letters, numbers, and underscore are allowed.");
          ok = false;
          if (firstBad == null)
            firstBad = usernameField;
        } else {
          int excludeId = (existing == null) ? -1 : existing.getId();
          if (usernameExists(uname, excludeId)) {
            setErr(userErr, "Username is already taken.");
            ok = false;
            if (firstBad == null)
              firstBad = usernameField;
          }
        }
      }

      if (role == null) {
        setErr(roleErr, "Role is required.");
        ok = false;
        if (firstBad == null)
          firstBad = (JComponent) roleCombo;
      }

      boolean passwordRequired = (existing == null);

      if (passwordRequired) {
        if (pw.length == 0) {
          setErr(passErr, "Password is required.");
          ok = false;
          if (firstBad == null)
            firstBad = passwordField;
        } else if (pw.length < PASSWORD_MIN) {
          setErr(passErr, "Password must be at least " + PASSWORD_MIN + " characters.");
          ok = false;
          if (firstBad == null)
            firstBad = passwordField;
        }

        if (cf.length == 0) {
          setErr(confErr, "Please confirm the password.");
          ok = false;
          if (firstBad == null)
            firstBad = confirmPasswordField;
        }
      } else {

        if (pw.length > 0 && pw.length < PASSWORD_MIN) {
          setErr(passErr, "Password must be at least " + PASSWORD_MIN + " characters.");
          ok = false;
          if (firstBad == null)
            firstBad = passwordField;
        }
      }

      if (pw.length > 0 || cf.length > 0) {
        if (!Arrays.equals(pw, cf)) {
          setErr(confErr, "Passwords do not match.");
          ok = false;
          if (firstBad == null)
            firstBad = confirmPasswordField;
        }
      }

      if (!ok && focusFirstBad && firstBad != null) {
        firstBad.requestFocusInWindow();
      }

      return ok;
    }

    private void addValidateOnBlur(JComponent c) {
      c.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
          validateForm(false);
        }
      });
    }

    private JLabel errLabel() {
      JLabel l = new JLabel(" ");
      l.setForeground(ERR);
      l.setFont(l.getFont().deriveFont(12f));
      l.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
      return l;
    }

    private void setErr(JLabel lbl, String msg) {
      lbl.setText((msg == null || msg.isBlank()) ? " " : msg);
    }

    private void clearErrors() {
      setErr(fnameErr, " ");
      setErr(lnameErr, " ");
      setErr(userErr, " ");
      setErr(passErr, " ");
      setErr(confErr, " ");
      setErr(roleErr, " ");
    }

    private void enforceFieldSize(JComponent c) {
      c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      c.setPreferredSize(new Dimension(0, 42));
      c.setAlignmentX(LEFT_ALIGNMENT);
    }
  }

  private boolean usernameExists(String username, int excludeId) {
    List<User> users = usersDAO.getAll();
    for (User u : users) {
      if (u.getId() == excludeId)
        continue;
      if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
        return true;
      }
    }
    return false;
  }

  private void wipe(char[] arr) {
    if (arr != null)
      Arrays.fill(arr, '\0');
  }
}
