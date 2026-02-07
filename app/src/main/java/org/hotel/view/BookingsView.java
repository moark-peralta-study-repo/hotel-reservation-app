package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.hotel.dto.BookingRowDTO;
import org.hotel.model.BookingStatus;
import org.hotel.model.BookingsViewMode;

public class BookingsView extends JPanel {
  private JTable bookingTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;
  private JButton addReservationBtn, checkInBtn, checkOutBtn, editReservationBtn, cancelReservationBtn;
  private JComboBox<Object> statusFilter;
  private TableRowSorter<DefaultTableModel> sorter;

  public BookingsView(List<BookingRowDTO> rows, BookingsViewMode mode) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#fffafb"));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    statusFilter = new JComboBox<>();
    statusFilter.addItem("All");
    for (BookingStatus status : BookingStatus.values()) {
      statusFilter.addItem(status);
    }

    statusFilter.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(
          JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof BookingStatus status) {
          String label = status.name().toLowerCase().replace('_', ' ');
          label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
          setText("Filter by (" + label + ")");
        } else if ("All".equals(value)) {
          setText("Filter by (All)");
        }
        return this;
      }
    });

    if (mode == null)
      mode = BookingsViewMode.ALL;

    switch (mode) {
      case RESERVATION -> {
        addReservationBtn = createActionButton("Add Reservation");
        editReservationBtn = createActionButton("Edit Reservation");
        cancelReservationBtn = createActionButton("Cancel Reservation");
        actionPanel.add(addReservationBtn);
        actionPanel.add(editReservationBtn);
        actionPanel.add(cancelReservationBtn);
      }
      case CHECK_IN -> {
        checkInBtn = createActionButton("Check-in");
        actionPanel.add(checkInBtn);
      }
      case CHECK_OUT -> {
        checkOutBtn = createActionButton("Check-Out");
        actionPanel.add(checkOutBtn);
      }
      case ALL -> {
        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 22));
        actionPanel.add(statusFilter);
      }
    }

    add(actionPanel, BorderLayout.NORTH);

    String[] columns = {
        "#", "Booking ID", "Customer", "Room No.", "Check-in", "Check-out", "Total Price", "Status"
    };

    Object[][] tableData = new Object[rows.size()][columns.length];

    for (int i = 0; i < rows.size(); i++) {
      BookingRowDTO r = rows.get(i);
      tableData[i][0] = i + 1;
      tableData[i][1] = r.getBookingId();
      tableData[i][2] = r.getCustomerName();
      tableData[i][3] = r.getRoomNumber();
      tableData[i][4] = r.getCheckIn();
      tableData[i][5] = r.getCheckOut();
      tableData[i][6] = r.getTotalPrice();
      tableData[i][7] = r.getStatus();
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

    sorter = new TableRowSorter<>(tableModel);
    bookingTable.setRowSorter(sorter);

    statusFilter.addActionListener(e -> applyStatusFilter());

    JScrollPane scrollPane = new JScrollPane(bookingTable);

    JPanel tableWrapper = new JPanel(new BorderLayout());
    tableWrapper.setBackground(Color.decode("#f9fafb"));
    tableWrapper.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
    tableWrapper.add(scrollPane, BorderLayout.CENTER);

    add(tableWrapper, BorderLayout.CENTER);

    statusFilter.setSelectedItem("All");
  }

  private void applyStatusFilter() {
    Object selected = statusFilter.getSelectedItem();

    if (selected == null || Objects.equals(selected, "All")) {
      sorter.setRowFilter(null);
      return;
    }

    BookingStatus desired = (BookingStatus) selected;
    final int STATUS_COL = 7;

    sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
      @Override
      public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
        Object value = entry.getValue(STATUS_COL);
        return value == desired;
      }
    });
  }

  private JButton createActionButton(String text) {
    JButton btn = new JButton(text);
    var base = UIManager.getFont("Button.font");
    btn.setFont(base.deriveFont(Font.PLAIN, base.getSize() + 1f));
    return btn;
  }

  public JTable getBookingTable() {
    return bookingTable;
  }

  public DefaultTableModel getTableModel() {
    return tableModel;
  }

  public JTextField getSearchField() {
    return searchField;
  }

  public JButton getAddReservationBtn() {
    return addReservationBtn;
  }

  public JButton getCheckInBtn() {
    return checkInBtn;
  }

  public JButton getCheckOutBtn() {
    return checkOutBtn;
  }

  public JButton getEditReservationBtn() {
    return editReservationBtn;
  }

  public JButton getCancelReservationBtn() {
    return cancelReservationBtn;
  }
}
