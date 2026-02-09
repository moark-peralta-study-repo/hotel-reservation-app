package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.hotel.model.User;
import org.hotel.utils.BookingUtils;

public class UsersView extends JPanel {
  private JTable usersTable;
  private DefaultTableModel tableModel;
  private JButton addUserBtn, editUserBtn, deleteUserBtn;

  public UsersView(List<User> rows) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#f9fafb"));

    setBorder(new EmptyBorder(25, 30, 25, 30));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    actionPanel.setOpaque(false);

    addUserBtn = createActionButton("Add User");
    editUserBtn = createActionButton("Edit User");
    deleteUserBtn = createActionButton("Delete User");

    actionPanel.add(addUserBtn);
    actionPanel.add(editUserBtn);
    actionPanel.add(deleteUserBtn);

    JPanel top = new JPanel(new BorderLayout());
    top.setOpaque(false);

    top.add(actionPanel, BorderLayout.NORTH);
    top.add(
        BookingUtils.buildHeader(
            "Users",
            "Manage user accounts and access roles"),
        BorderLayout.SOUTH);

    add(top, BorderLayout.NORTH);

    String[] columns = {
        "#",
        "User ID",
        "First Name",
        "Last Name",
        "Username",
        "Password",
        "Role"
    };

    Object[][] tableData = new Object[rows.size()][columns.length];

    for (int i = 0; i < rows.size(); i++) {
      User u = rows.get(i);
      tableData[i][0] = i + 1;
      tableData[i][1] = u.getId();
      tableData[i][2] = u.getFirstName();
      tableData[i][3] = u.getLastName();
      tableData[i][4] = u.getUsername();
      tableData[i][5] = new String(u.getPassword());
      tableData[i][6] = u.getRole();
    }

    tableModel = new DefaultTableModel(tableData, columns) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };

    usersTable = new JTable(tableModel);

    usersTable.getColumnModel().getColumn(1).setMinWidth(0);
    usersTable.getColumnModel().getColumn(1).setMaxWidth(0);
    usersTable.getColumnModel().getColumn(1).setWidth(0);

    usersTable.getColumnModel().getColumn(6).setCellRenderer(new UserRolePillRenderer());

    usersTable.setFillsViewportHeight(true);
    usersTable.setRowHeight(36);
    usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(usersTable);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#e5e7eb")));
    scrollPane.getViewport().setBackground(Color.WHITE);

    JPanel tableWrapper = new JPanel(new BorderLayout());
    tableWrapper.setOpaque(false);
    tableWrapper.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
    tableWrapper.add(scrollPane, BorderLayout.CENTER);

    add(tableWrapper, BorderLayout.CENTER);
  }

  private JButton createActionButton(String text) {
    JButton btn = new JButton(text);
    var base = UIManager.getFont("Button.font");
    btn.setFont(base.deriveFont(Font.PLAIN, base.getSize() + 1f));
    return btn;
  }

  public JTable getUsersTable() {
    return usersTable;
  }

  public DefaultTableModel getTableModel() {
    return tableModel;
  }

  public JButton getAddUserBtn() {
    return addUserBtn;
  }

  public JButton getEditUserBtn() {
    return editUserBtn;
  }

  public JButton getDeleteUserBtn() {
    return deleteUserBtn;
  }
}
