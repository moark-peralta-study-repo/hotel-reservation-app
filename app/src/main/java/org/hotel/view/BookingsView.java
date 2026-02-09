package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.hotel.dto.BookingRowDTO;
import org.hotel.model.BookingSort;
import org.hotel.model.BookingStatus;
import org.hotel.model.BookingsViewMode;
import org.hotel.utils.BookingUtils;

public class BookingsView extends JPanel {

  private JTable bookingTable;
  private DefaultTableModel tableModel;
  private JTextField searchField;

  private JButton addReservationBtn, checkInBtn, checkOutBtn, editReservationBtn, cancelReservationBtn;
  private JButton prevBtn, nextBtn;
  private JLabel pageLbl;

  private JComboBox<Object> statusFilter;
  private JComboBox<Object> sortFilter;

  public BookingsView(List<BookingRowDTO> rows, BookingsViewMode mode) {
    setLayout(new BorderLayout());
    setBackground(Color.decode("#f9fafb"));
    setBorder(new EmptyBorder(25, 30, 25, 30));

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    actionPanel.setBackground(Color.decode("#f9fafb"));

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
        } else {
          setText("Filter by (All)");
        }
        return this;
      }
    });

    if (mode == null) {
      mode = BookingsViewMode.ALL;
    }

    switch (mode) {
      case RESERVATION -> {
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search customer...");
        addReservationBtn = createActionButton("Add Reservation");
        editReservationBtn = createActionButton("Edit Reservation");
        cancelReservationBtn = createActionButton("Cancel Reservation");
        actionPanel.add(addReservationBtn);
        actionPanel.add(editReservationBtn);
        actionPanel.add(cancelReservationBtn);
        actionPanel.add(sortFilter);
        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
      }
      case CHECK_IN -> {
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search customer...");
        checkInBtn = createActionButton("Check-in");
        actionPanel.add(checkInBtn);
        actionPanel.add(sortFilter);
        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
      }
      case CHECK_OUT -> {
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search customer...");
        checkOutBtn = createActionButton("Check-Out");
        actionPanel.add(checkOutBtn);
        actionPanel.add(sortFilter);
        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
      }
      case ALL -> {
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search customer...");
        actionPanel.add(statusFilter);
        actionPanel.add(sortFilter);
        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
      }
    }

    prevBtn = createActionButton("Prev");
    nextBtn = createActionButton("Next");
    pageLbl = new JLabel("Page 1");

    JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    paginationPanel.setOpaque(false);
    paginationPanel.add(prevBtn);
    paginationPanel.add(pageLbl);
    paginationPanel.add(nextBtn);

    String title;
    String subtitle;

    switch (mode) {
      case RESERVATION -> {
        title = "Reservations";
        subtitle = "Upcoming and future reservations";
      }
      case CHECK_IN -> {
        title = "Check-ins";
        subtitle = "Guests arriving today or overdue";
      }
      case CHECK_OUT -> {
        title = "Check-outs";
        subtitle = "Guests currently staying";
      }
      default -> {
        title = "Bookings";
        subtitle = "View and manage all bookings";
      }
    }

    JPanel top = new JPanel(new BorderLayout());
    top.setOpaque(false);
    top.add(actionPanel, BorderLayout.NORTH);
    top.add(BookingUtils.buildHeader(title, subtitle), BorderLayout.SOUTH);
    add(top, BorderLayout.NORTH);

    tableModel = new DefaultTableModel(buildData(rows), new Object[] {
        "#", "Booking ID", "Customer", "Room No.", "Check-in", "Check-out", "Total Price", "Status"
    }) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };

    bookingTable = new JTable(tableModel);
    bookingTable.getColumnModel().getColumn(7).setCellRenderer(new BookingStatusPillRenderer());
    bookingTable.getColumnModel().getColumn(1).setMinWidth(0);
    bookingTable.getColumnModel().getColumn(1).setMaxWidth(0);
    bookingTable.getColumnModel().getColumn(1).setWidth(0);
    bookingTable.setRowHeight(36);
    bookingTable.setFillsViewportHeight(true);
    bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(bookingTable);

    JPanel tableWrapper = new JPanel(new BorderLayout());
    tableWrapper.setBackground(Color.decode("#f9fafb"));
    tableWrapper.add(scrollPane, BorderLayout.CENTER);
    tableWrapper.add(paginationPanel, BorderLayout.SOUTH);

    add(tableWrapper, BorderLayout.CENTER);

    if (statusFilter != null) {
      statusFilter.setSelectedItem("All");
    }
  }

  private Object[][] buildData(List<BookingRowDTO> rows) {
    Object[][] data = new Object[rows.size()][8];
    for (int i = 0; i < rows.size(); i++) {
      BookingRowDTO r = rows.get(i);
      data[i][0] = i + 1;
      data[i][1] = r.getBookingId();
      data[i][2] = r.getCustomerName();
      data[i][3] = r.getRoomNumber();
      data[i][4] = r.getCheckIn();
      data[i][5] = r.getCheckOut();
      data[i][6] = r.getTotalPrice();
      data[i][7] = r.getStatus();
    }
    return data;
  }

  private JButton createActionButton(String text) {
    JButton btn = new JButton(text);
    var base = UIManager.getFont("Button.font");
    btn.setFont(base.deriveFont(Font.PLAIN, base.getSize() + 1f));
    return btn;
  }

  public void setRows(List<BookingRowDTO> rows) {
    tableModel.setDataVector(buildData(rows), new Object[] {
        "#", "Booking ID", "Customer", "Room No.", "Check-in", "Check-out", "Total Price", "Status"
    });

    bookingTable.getColumnModel().getColumn(1).setMinWidth(0);
    bookingTable.getColumnModel().getColumn(1).setMaxWidth(0);
    bookingTable.getColumnModel().getColumn(1).setWidth(0);

    bookingTable.getColumnModel().getColumn(7).setCellRenderer(new BookingStatusPillRenderer());
    bookingTable.setRowHeight(36);
  }

  public void setSortSelected(BookingSort sort) {
    if (sortFilter == null) {
      return;
    }
    if (sort == null) {
      sortFilter.setSelectedItem("Default");
    } else {
      sortFilter.setSelectedItem(sort);
    }
  }

  public Object getSelectedStatusFilter() {
    return statusFilter != null ? statusFilter.getSelectedItem() : null;
  }

  public Object getSelectedSortFilter() {
    return sortFilter != null ? sortFilter.getSelectedItem() : null;
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

  public JComboBox<Object> getStatusFilter() {
    return statusFilter;
  }

  public JComboBox<Object> getSortFilter() {
    return sortFilter;
  }
}
