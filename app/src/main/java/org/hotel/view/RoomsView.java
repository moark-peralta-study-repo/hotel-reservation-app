package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.hotel.model.Room;
import org.hotel.utils.BookingUtils;

public class RoomsView extends JPanel {
  private JTable roomTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;
  private JButton addBtn, editBtn, deleteBtn;

  public RoomsView(List<Room> rooms) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#f9fafb"));

    // Dashboard-style page padding (keeps everything aligned)
    setBorder(new EmptyBorder(25, 30, 25, 30));

    // ===== Action panel (top) =====
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    actionPanel.setOpaque(false); // inherit background for consistent look

    addBtn = createActionButton("Add Room");
    editBtn = createActionButton("Edit Room");
    deleteBtn = createActionButton("Delete Room");

    searchField = new JTextField(18);
    searchField.putClientProperty("JTextField.placeholderText", "Search room...");

    actionPanel.add(addBtn);
    actionPanel.add(editBtn);
    actionPanel.add(deleteBtn);
    actionPanel.add(new JLabel("Search:"));
    actionPanel.add(searchField);

    JPanel top = new JPanel(new BorderLayout());
    top.setOpaque(false);

    top.add(actionPanel, BorderLayout.NORTH);
    top.add(
        BookingUtils.buildHeader(
            "Rooms",
            "View and manage hotel room availability"),
        BorderLayout.SOUTH);

    add(top, BorderLayout.NORTH);

    String[] columns = { "#", "ID", "Room No.", "Type", "Price", "Status" };
    Object[][] tableData = new Object[rooms.size()][columns.length];

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
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };

    roomTable = new JTable(tableModel);

    roomTable.getColumnModel().getColumn(1).setMinWidth(0);
    roomTable.getColumnModel().getColumn(1).setMaxWidth(0);
    roomTable.getColumnModel().getColumn(1).setWidth(0);

    roomTable.getColumnModel().getColumn(5).setCellRenderer(new RoomStatusPillRenderer());

    roomTable.setFillsViewportHeight(true);
    roomTable.setRowHeight(36);
    roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(roomTable);
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

  public void setRooms(List<Room> rooms) {
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

    tableModel.setDataVector(tableData, new Object[] { "#", "ID", "Room No.", "Type", "Price", "Status" });

    roomTable.getColumnModel().getColumn(1).setMinWidth(0);
    roomTable.getColumnModel().getColumn(1).setMaxWidth(0);
    roomTable.getColumnModel().getColumn(1).setWidth(0);

    roomTable.getColumnModel().getColumn(5).setCellRenderer(new RoomStatusPillRenderer());
    roomTable.setRowHeight(36);
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
