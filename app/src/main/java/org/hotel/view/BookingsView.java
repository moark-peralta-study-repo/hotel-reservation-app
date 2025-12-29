package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.hotel.model.Booking;

public class BookingsView extends JPanel {
  private JTable bookingTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;

  public BookingsView(List<Booking> bookings) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#f9fafb"));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    add(actionPanel, BorderLayout.NORTH);

    // this.id = id;
    // this.customerId = customerId;
    // this.roomId = roomId;
    // this.checkIn = checkIn;
    // this.checkOut = checkOut;
    // this.totalPrice = totalPrice;
    // this.status = status;

    String[] columns = {
        "#",
        "Booking ID",
        "Customer",
        "Room No.",
        "Check-in",
        "Check-out",
        "Total Price",
        "Status" };

    Object[][] tableData = new Object[bookings.size()][columns.length];

    for (int i = 0; i < bookings.size(); i++) {
      Booking b = bookings.get(i);

      tableData[i][0] = i + 1;
      tableData[i][1] = b.getId();
      tableData[i][2] = b.getCustomerId();
      tableData[i][3] = b.getRoomId();
      tableData[i][4] = b.getCheckIn();
      tableData[i][5] = b.getCheckOut();
      tableData[i][6] = b.getTotalPrice();
      tableData[i][7] = b.getStatus();
    }

    tableModel = new DefaultTableModel(tableData, columns) {
      @Override
      public boolean isCellEditable(int row, int cols) {
        return false;
      }
    };

    bookingTable = new JTable(tableModel);
    bookingTable.getColumnModel().getColumn(1).setMinWidth(0);
    bookingTable.getColumnModel().getColumn(1).setMaxWidth(0);
    bookingTable.getColumnModel().getColumn(1).setWidth(0);

    bookingTable.setFillsViewportHeight(true);
    bookingTable.setRowHeight(30);
    bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(bookingTable);
    add(scrollPane, BorderLayout.CENTER);
  }

  public void updateTable(List<Booking> bookings) {
    Object[][] tableData = new Object[bookings.size()][tableModel.getColumnCount()];

    for (int i = 0; i < bookings.size(); i++) {
      Booking b = bookings.get(i);
      tableData[i][0] = i + 1;
      tableData[i][1] = b.getId();
      tableData[i][2] = b.getCustomerId();
      tableData[i][3] = b.getRoomId();
      tableData[i][4] = b.getCheckIn();
      tableData[i][5] = b.getCheckOut();
      tableData[i][6] = b.getTotalPrice();
      tableData[i][7] = b.getStatus();
    }

    tableModel.setDataVector(tableData,
        new Object[] { "#", "Booking ID", "Customer", "Room No.", "Check-in", "Check-out", "Total Price", "Status" });
  }
}
