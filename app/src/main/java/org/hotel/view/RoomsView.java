package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.hotel.model.Room;

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

    String[] columns = { "#", "ID", "Room No.", "Type", "Price", "Status" };

    Object[][] tableData = new Object[rooms.size()][6];

    for (int i = 0; i < rooms.size(); i++) {
      Room r = rooms.get(i);

      tableData[i][0] = i + 1;
      tableData[i][1] = r.getId();
      tableData[i][2] = r.getRoomNumber();
      tableData[i][3] = r.getType();
      tableData[i][4] = r.getPrice();
      tableData[i][5] = r.isAvailable() ? "Available" : "Occupied";
    }

    tableModel = new DefaultTableModel(tableData, columns) {
      @Override
      public boolean isCellEditable(int row, int cols) {
        return false;
      }
    };

    roomTable = new JTable(tableModel);
    roomTable.getColumnModel().getColumn(1).setMinWidth(0);
    roomTable.getColumnModel().getColumn(1).setMaxWidth(0);
    roomTable.getColumnModel().getColumn(1).setWidth(0);

    roomTable.setFillsViewportHeight(true);
    roomTable.setRowHeight(30);
    roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(roomTable);

    JPanel tableWrapper = new JPanel(new BorderLayout());
    tableWrapper.setBackground(Color.decode("#f9fafb"));
    tableWrapper.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

    tableWrapper.add(scrollPane, BorderLayout.CENTER);
    add(tableWrapper, BorderLayout.CENTER);
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
