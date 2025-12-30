package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.hotel.model.Booking;
import org.hotel.model.BookingsViewMode;

public class BookingsView extends JPanel {
  private JTable bookingTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;
  private JButton addReservationBtn, checkInBtn, checkOutBtn, editReservationBtn, cancelReservationBtn;

  public BookingsView(List<Booking> bookings, BookingsViewMode mode) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#f9fafb"));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    switch (mode) {
      case RESERVATION -> {
        addReservationBtn = new JButton("Add Reservation");
        editReservationBtn = new JButton("Edit Reservation");
        cancelReservationBtn = new JButton("Cancel Reservation");

        actionPanel.add(addReservationBtn);
        actionPanel.add(editReservationBtn);
        actionPanel.add(cancelReservationBtn);
      }

      case CHECK_IN -> {
        checkInBtn = new JButton("Check In");
        actionPanel.add(checkInBtn);
      }

      case CHECK_OUT -> {
        checkOutBtn = new JButton("Check Out");
        actionPanel.add(checkOutBtn);
      }
    }

    add(actionPanel, BorderLayout.NORTH);

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
}
