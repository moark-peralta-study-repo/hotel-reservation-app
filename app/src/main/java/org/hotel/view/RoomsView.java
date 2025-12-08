package org.hotel.view;

import org.hotel.model.Room;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class RoomsView extends JPanel {
  private JTable roomTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;
  private JButton addBtn, editBtn, deleteBtn;

  public RoomsView(List<Room> rooms) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#f9fafb"));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    addBtn = new JButton("Add Room");
    editBtn = new JButton("Edit Room");
    deleteBtn = new JButton("Delete Room");

    searchField = new JTextField(15);

    actionPanel.add(addBtn);
    actionPanel.add(editBtn);
    actionPanel.add(deleteBtn);
    actionPanel.add(new JLabel("Search:"));
    actionPanel.add(searchField);

    add(actionPanel, BorderLayout.NORTH);

    String[] columns = { "#", "Room No.", "Type", "Price", "Status" };

    Object[][] tableData = new Object[rooms.size()][5];

    for (int i = 0; i < rooms.size(); i++) {
      Room r = rooms.get(i);
      tableData[i][0] = i + 1;
      tableData[i][1] = r.getRoomNumber();
      tableData[i][2] = r.getType();
      tableData[i][3] = r.getPrice();
      tableData[i][4] = r.isAvailable() ? "Available" : "Occupied";
    }

    tableModel = new DefaultTableModel(tableData, columns) {
      @Override
      public boolean isCellEditable(int row, int cols) {
        return false;
      }
    };

    roomTable = new JTable(tableModel);
    roomTable.setFillsViewportHeight(true);
    roomTable.setRowHeight(30);

    JScrollPane scrollPane = new JScrollPane(roomTable);
    add(scrollPane, BorderLayout.CENTER);
  }

  public JTable getRoomTable() {
    return roomTable;
  }

  public DefaultTableModel getTableModel() {
    return tableModel;
  }

  public JTextField getSearchField() {
    return searchField;
  }

  public JButton getAddBtn() {
    return addBtn;
  }

  public JButton getEditBtn() {
    return editBtn;
  }

  public JButton getDeleteBtn() {
    return deleteBtn;
  }

}
