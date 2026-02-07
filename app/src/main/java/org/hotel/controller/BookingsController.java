package org.hotel.controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.hotel.dto.BookingRowDTO;
import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;
import org.hotel.model.BookingsViewMode;
import org.hotel.model.Customer;
import org.hotel.model.Room;
import org.hotel.model.dao.BookingsDAO;
import org.hotel.model.dao.CustomerDAO;
import org.hotel.model.dao.RoomDAO;
import org.hotel.view.AddReservationDialog;
import org.hotel.view.BookingsView;
import org.hotel.view.EditReservationDialog;
import org.hotel.view.MainFrame;

public class BookingsController {
  private MainFrame mainFrame;
  private BookingsDAO bookingsDAO;
  private BookingsView bookingsView;
  private BookingsViewMode currentMode = BookingsViewMode.ALL;
  private BookingStatus currentStatusFilter = null;
  private int PAGE = 1;
  private final int PAGE_SIZE = 20;
  private int TOTAL = 0;
  private String currentSearch = "";

  public BookingsController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.bookingsDAO = new BookingsDAO();
    new CustomerDAO();
    new RoomDAO();

    registerEvents();
  }

  private void loadBookingsPage() {
    bookingsDAO.autoCancelExpiredReservations();

    int offset = (PAGE - 1) * PAGE_SIZE;

    TOTAL = bookingsDAO.getTotalCount(currentMode, currentStatusFilter, currentSearch);

    List<BookingRowDTO> rows = bookingsDAO.getPage(currentMode, currentStatusFilter, currentSearch, PAGE_SIZE, offset);

    bookingsView.setRows(rows);

    int totalPages = Math.max(1, (int) Math.ceil(TOTAL / (double) PAGE_SIZE));
    bookingsView.getPageLabel().setText("Page " + PAGE + " / " + totalPages);

    bookingsView.getPrevBtn().setEnabled(PAGE > 1);
    bookingsView.getNextBtn().setEnabled(PAGE < totalPages);
  }

  private void registerEvents() {
    mainFrame.getBookingsBtn().addActionListener(e -> loadBookings());
    mainFrame.getCheckInBtn().addActionListener(e -> loadPendingCheckInBookings());
    mainFrame.getCheckOutBtn().addActionListener(e -> loadCheckedInBookings());
    mainFrame.getReservationBtn().addActionListener(e -> loadFutureBookings());
  }

  private void loadCheckedInBookings() {
    currentMode = BookingsViewMode.CHECK_OUT;
    currentStatusFilter = null;
    PAGE = 1;

    List<BookingRowDTO> checkedInBookings = bookingsDAO.getCheckedInBookingsRow();
    bookingsView = new BookingsView(checkedInBookings, BookingsViewMode.CHECK_OUT);

    attachViewListeners();
    loadBookingsPage();

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void loadFutureBookings() {
    currentMode = BookingsViewMode.RESERVATION;
    currentStatusFilter = null;
    PAGE = 1;

    List<BookingRowDTO> futureCheckin = bookingsDAO.getReservedBookingRows();
    bookingsView = new BookingsView(futureCheckin, BookingsViewMode.RESERVATION);

    loadBookingsPage();
    attachViewListeners();

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void loadPendingCheckInBookings() {
    currentMode = BookingsViewMode.CHECK_IN;
    currentStatusFilter = null;
    PAGE = 1;

    List<BookingRowDTO> pendingCheckin = bookingsDAO.getPendingCheckInRows();
    bookingsView = new BookingsView(pendingCheckin, BookingsViewMode.CHECK_IN);

    loadBookingsPage();
    attachViewListeners();

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void loadBookings() {
    currentMode = BookingsViewMode.ALL;
    currentStatusFilter = null;
    PAGE = 1;

    bookingsView = new BookingsView(List.of(), BookingsViewMode.ALL);

    attachViewListeners();

    showBookingsView();
    loadBookingsPage();
  }

  private void showBookingsView() {
    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void attachViewListeners() {
    if (bookingsView.getStatusFilter() != null) {
      bookingsView.getStatusFilter().addActionListener(e -> {
        Object selected = bookingsView.getSelectedStatusFilter();
        currentStatusFilter = (selected instanceof BookingStatus bs) ? bs : null;
        PAGE = 1;
        loadBookingsPage();
      });
    }

    if (bookingsView.getSearchField() != null) {
      bookingsView.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
        private void changed() {
          currentSearch = bookingsView.getSearchField().getText().trim();
          PAGE = 1;
          loadBookingsPage();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
          changed();
        };

        @Override
        public void removeUpdate(DocumentEvent e) {
          changed();
        };

        @Override
        public void changedUpdate(DocumentEvent e) {
          changed();
        };
      });
    }

    if (bookingsView.getPrevBtn() != null) {
      bookingsView.getPrevBtn().addActionListener(e -> {
        if (PAGE > 1) {
          PAGE--;
          loadBookingsPage();
        }
      });
    }

    if (bookingsView.getNextBtn() != null) {
      bookingsView.getNextBtn().addActionListener(e -> {
        int totalPages = Math.max(1, (int) Math.ceil(TOTAL / (double) PAGE_SIZE));
        if (PAGE < totalPages) {
          PAGE++;
          loadBookingsPage();
        }
      });
    }

    if (bookingsView.getCheckInBtn() != null) {
      bookingsView.getCheckInBtn().addActionListener(e -> handleCheckIn());
    }

    if (bookingsView.getCheckOutBtn() != null) {
      bookingsView.getCheckOutBtn().addActionListener(e -> handleCheckOut());
    }

    if (bookingsView.getCancelReservationBtn() != null) {
      bookingsView.getCancelReservationBtn().addActionListener(e -> handleCancelReservation());
    }

    if (bookingsView.getAddReservationBtn() != null) {
      bookingsView.getAddReservationBtn().addActionListener(e -> openAddReservationDialog());
    }

    if (bookingsView.getEditReservationBtn() != null) {
      bookingsView.getEditReservationBtn().addActionListener(e -> openEditReservationDialog());
    }
  }

  private void handleCancelReservation() {
    int selectedRow = bookingsView.getBookingTable().getSelectedRow();
    if (selectedRow != -1) {
      int bookingId = (int) bookingsView.getTableModel().getValueAt(selectedRow, 1);
      Booking booking = bookingsDAO.getById(bookingId);

      if (booking != null) {
        bookingsDAO.cancelReservation(booking);
        JOptionPane.showMessageDialog(mainFrame, "Cancelled reservation with Booking ID: " + bookingId);
        loadFutureBookings();
      }
    } else {
      JOptionPane.showMessageDialog(mainFrame, "Please select a reservation to cancel.");
    }
  }

  private void handleCheckIn() {
    int selectedRow = bookingsView.getBookingTable().getSelectedRow();
    if (selectedRow != -1) {
      int bookingId = (int) bookingsView.getTableModel().getValueAt(selectedRow, 1);
      Booking booking = bookingsDAO.getById(bookingId);

      if (booking != null) {
        bookingsDAO.checkInCustomer(booking);
        JOptionPane.showMessageDialog(mainFrame, "Checked in Booking ID: " + bookingId);
        loadPendingCheckInBookings();
      }
    } else {
      JOptionPane.showMessageDialog(mainFrame, "Please select a booking to check in.");
    }
  }

  private void handleCheckOut() {
    int selectedRow = bookingsView.getBookingTable().getSelectedRow();
    if (selectedRow != -1) {
      int bookingId = (int) bookingsView.getTableModel().getValueAt(selectedRow, 1);
      Booking booking = bookingsDAO.getById(bookingId);

      if (booking != null) {
        bookingsDAO.checkOutCustomer(booking);
        JOptionPane.showMessageDialog(mainFrame, "Checked out Booking ID: " + bookingId);
        loadCheckedInBookings();
      }
    } else {
      JOptionPane.showMessageDialog(mainFrame, "Please select a booking to check out.");
    }
  }

  private void openAddReservationDialog() {
    // 1) Controller prepares data for the view
    List<Room> rooms = new RoomDAO().getAll();

    // For disabling UI dates: only RESERVED + CHECKED_IN ranges
    // (implement this method in BookingsDAO; shown below)
    List<AddReservationDialog.RoomBookingRange> ranges = bookingsDAO.getActiveRoomRanges();

    // 2) View (dialog) is UI only
    AddReservationDialog dialog = new AddReservationDialog(mainFrame, rooms, ranges);
    dialog.setVisible(true);

    // 3) Controller consumes view result
    AddReservationDialog.ReservationRequest req = dialog.getResult();
    if (req == null)
      return; // cancelled

    // 4) Final source of truth: DB overlap check
    RoomDAO roomDAO = new RoomDAO();
    boolean ok = roomDAO.isRoomAvailableForRange(req.roomId, req.checkIn.toString(), req.checkOut.toString());
    if (!ok) {
      JOptionPane.showMessageDialog(mainFrame,
          "That room is no longer available for the selected dates.\nPlease choose another room/date range.",
          "Room Not Available",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    // 5) Insert customer + booking
    CustomerDAO customerDAO = new CustomerDAO();
    int customerId = customerDAO.insertAndReturnId(new Customer(req.customerName, req.phone, req.email));
    if (customerId <= 0) {
      JOptionPane.showMessageDialog(mainFrame, "Failed to save customer.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    Booking booking = new Booking(
        0,
        customerId,
        req.roomId,
        req.checkIn.toString(),
        req.checkOut.toString(),
        req.totalPrice,
        BookingStatus.RESERVED);

    bookingsDAO.insert(booking);

    loadFutureBookings();
  }

  private void openEditReservationDialog() {
    int selectedRow = bookingsView.getBookingTable().getSelectedRow();

    if (selectedRow != -1) {
      int bookingId = (int) bookingsView.getTableModel().getValueAt(selectedRow, 1);
      Booking booking = bookingsDAO.getById(bookingId);

      EditReservationDialog dialog = new EditReservationDialog(mainFrame, booking, bookingsDAO);
      dialog.setVisible(true);

      loadFutureBookings();
    } else {
      JOptionPane.showMessageDialog(mainFrame, "Please select a Reservation to edit");
    }
  }
}
