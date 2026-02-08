package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.hotel.dto.BookingRowDTO;
import org.hotel.model.BookingSort;
import org.hotel.model.BookingStatus;
import org.hotel.model.BookingsViewMode;

public class BookingsView extends JPanel {
  private JTable bookingTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;
  private JButton addReservationBtn, checkInBtn, checkOutBtn, editReservationBtn, cancelReservationBtn, prevBtn,
      nextBtn;
  private JLabel pageLbl;

  private JComboBox<Object> statusFilter;
  private JComboBox<Object> sortFilter;

  public BookingsView(List<BookingRowDTO> rows, BookingsViewMode mode) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#fffafb"));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    statusFilter = new JComboBox<>();
    statusFilter.addItem("All");

    sortFilter = new JComboBox<>();
    sortFilter.addItem("Default");
    sortFilter.addItem(BookingSort.CHECK_IN_ASC);
    sortFilter.addItem(BookingSort.CHECK_IN_DESC);
    sortFilter.addItem(BookingSort.CHECK_OUT_ASC);
    sortFilter.addItem(BookingSort.CHECK_OUT_DESC);

    for (BookingStatus status : BookingStatus.values()) {
      statusFilter.addItem(status);
    }

    sortFilter.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(
          JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof BookingSort s) {
          String label = switch (s) {
            case CHECK_IN_ASC -> "Check-in (Oldest first)";
            case CHECK_IN_DESC -> "Check-in (Newest first)";
            case CHECK_OUT_ASC -> "Check-out (Oldest first)";
            case CHECK_OUT_DESC -> "Check-out (Newest first)";
          };
          setText("Sort by (" + label + ")");
        } else {
          setText("Sort by (Default)");
        }
        return this;
      }
    });

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

        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Seach customer...");
        addReservationBtn = createActionButton("Add Reservation");
        editReservationBtn = createActionButton("Edit Reservation");
        cancelReservationBtn = createActionButton("Cancel Reservation");
        actionPanel.add(addReservationBtn);
        actionPanel.add(editReservationBtn);
        actionPanel.add(cancelReservationBtn);

        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
        actionPanel.add(sortFilter);
      }
      case CHECK_IN -> {
        checkInBtn = createActionButton("Check-in");
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Seach customer...");

        actionPanel.add(checkInBtn);

        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
        actionPanel.add(sortFilter);
      }
      case CHECK_OUT -> {
        checkOutBtn = createActionButton("Check-Out");

        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Seach customer...");
        actionPanel.add(checkOutBtn);

        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
        actionPanel.add(sortFilter);
      }
      case ALL -> {
        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 22));
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Seach customer...");
        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
        actionPanel.add(statusFilter);
        actionPanel.add(sortFilter);
      }
    }

    // Pagination Btns
    prevBtn = createActionButton("Prev");
    nextBtn = createActionButton("Next");
    pageLbl = new JLabel("Page 1");

    JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    paginationPanel.setOpaque(false);

    paginationPanel.add(prevBtn);
    paginationPanel.add(pageLbl);
    paginationPanel.add(nextBtn);

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
    bookingTable.getColumnModel().getColumn(7).setCellRenderer(new StatusPillRenderer());
    bookingTable.getColumnModel().getColumn(1).setMinWidth(0);
    bookingTable.getColumnModel().getColumn(1).setMaxWidth(0);
    bookingTable.getColumnModel().getColumn(1).setWidth(0);
    bookingTable.setFillsViewportHeight(true);
    bookingTable.setRowHeight(36);
    bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(bookingTable);

    JPanel tableWrapper = new JPanel(new BorderLayout());
    tableWrapper.setBackground(Color.decode("#f9fafb"));
    tableWrapper.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
    tableWrapper.add(scrollPane, BorderLayout.CENTER);
    tableWrapper.add(paginationPanel, BorderLayout.SOUTH);

    add(tableWrapper, BorderLayout.CENTER);

    statusFilter.setSelectedItem("All");
  }

  private JButton createActionButton(String text) {
    JButton btn = new JButton(text);
    var base = UIManager.getFont("Button.font");
    btn.setFont(base.deriveFont(Font.PLAIN, base.getSize() + 1f));
    return btn;
  }

  public void setRows(List<BookingRowDTO> rows) {
    Object[][] tableData = new Object[rows.size()][8];

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

    tableModel.setDataVector(tableData, new Object[] {
        "#", "Booking ID", "Customer", "Room No.", "Check-in", "Check-out", "Total Price", "Status"
    });

    bookingTable.getColumnModel().getColumn(1).setMinWidth(0);
    bookingTable.getColumnModel().getColumn(1).setMaxWidth(0);
    bookingTable.getColumnModel().getColumn(1).setWidth(0);

    bookingTable.getColumnModel().getColumn(7).setCellRenderer(new StatusPillRenderer());
    bookingTable.setRowHeight(36);
  }

  public JButton getPrevBtn() {
    return prevBtn;
  }

  public JButton getNextBtn() {
    return nextBtn;
  }

  public JLabel getPageLabel() {
    return pageLbl;
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

  public Object getSelectedStatusFilter() {
    return statusFilter != null ? statusFilter.getSelectedItem() : null;
  }

  public Object getSelectedSortFilter() {
    return sortFilter != null ? sortFilter.getSelectedItem() : null;
  }

  public JComboBox<Object> getStatusFilter() {
    return statusFilter;
  }

  public JComboBox<Object> getSortFilter() {
    return sortFilter;
  }
}
