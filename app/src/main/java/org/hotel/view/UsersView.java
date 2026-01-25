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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.hotel.model.User;

public class UsersView extends JPanel {
  private JTable usersTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;
  private JButton addUserBtn, editUserBtn, deleteUserBtn;

  public UsersView(List<User> rows) {
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    setLayout(new BorderLayout());

    addUserBtn = createActionButton("Add User");
    editUserBtn = createActionButton("Edit User");
    deleteUserBtn = createActionButton("Delete User");

    actionPanel.add(addUserBtn);
    actionPanel.add(editUserBtn);
    actionPanel.add(deleteUserBtn);

    add(actionPanel, BorderLayout.NORTH);

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
      tableData[i][5] = u.getPassword();
      tableData[i][6] = u.getRole();
    }

    tableModel = new DefaultTableModel(tableData, columns) {
      @Override
      public boolean isCellEditable(int row, int cols) {
        return false;
      }
    };

    usersTable = new JTable(tableModel);

    usersTable.getColumnModel().getColumn(1).setMinWidth(0);
    usersTable.getColumnModel().getColumn(1).setMaxWidth(0);
    usersTable.getColumnModel().getColumn(1).setWidth(0);

    usersTable.setFillsViewportHeight(true);
    usersTable.setRowHeight(30);
    usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(usersTable);

    JPanel tableWrapper = new JPanel(new BorderLayout());
    tableWrapper.setBackground(Color.decode("#f9fafb"));
    tableWrapper.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

    tableWrapper.add(scrollPane, BorderLayout.CENTER);
    add(tableWrapper, BorderLayout.CENTER);
  }

  private JButton createActionButton(String text) {
    JButton btn = new JButton(text);

    var base = UIManager.getFont("Button.font");
    btn.setFont(base.deriveFont(Font.PLAIN, base.getSize() + 1f));

    return btn;
  }
}
