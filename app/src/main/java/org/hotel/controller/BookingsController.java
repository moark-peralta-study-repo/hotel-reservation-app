package org.hotel.controller;

import java.util.List;

import javax.swing.JOptionPane;

import org.hotel.dto.BookingRowDTO;
import org.hotel.model.Booking;
import org.hotel.model.BookingsViewMode;
import org.hotel.model.dao.BookingsDAO;
import org.hotel.view.AddReservationDialog;
import org.hotel.view.BookingsView;
import org.hotel.view.EditReservationDialog;
import org.hotel.view.MainFrame;

public class BookingsController {
  private MainFrame mainFrame;
  private BookingsDAO bookingsDAO;
  private Booking booking;
  private BookingsView bookingsView;

  public BookingsController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.bookingsDAO = new BookingsDAO();

    registerEvents();
  }

  private void registerEvents() {
    mainFrame.getBookingsBtn().addActionListener(e -> loadBookings());

    mainFrame.getCheckInBtn().addActionListener(e -> loadPendingCheckInBookings());

    mainFrame.getCheckOutBtn().addActionListener(e -> loadCheckedInBookings());

    mainFrame.getReservationBtn().addActionListener(e -> loadFutureBookings());
  }

  private void loadCheckedInBookings() {
    List<Booking> checkedInBookings = bookingsDAO.getCheckedInBookings();
    bookingsView = new BookingsView(checkedInBookings, BookingsViewMode.CHECK_OUT);

    attachViewListeners();

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  // Future Checkins
  private void loadFutureBookings() {
    List<Booking> futureCheckin = bookingsDAO.getReservedBookings();
    bookingsView = new BookingsView(futureCheckin, BookingsViewMode.RESERVATION);

    attachViewListeners();

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  // Arriving today OR overdue checkins
  private void loadPendingCheckInBookings() {
    List<BookingRowDTO> pendingCheckin = bookingsDAO.getPendingCheckInRows();
    bookingsView = new BookingsView(pendingCheckin, BookingsViewMode.CHECK_IN);

    attachViewListeners();

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void loadBookings() {
    List<Booking> bookings = bookingsDAO.getAll();
    bookingsView = new BookingsView(bookings, BookingsViewMode.ALL);

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void attachViewListeners() {
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
        loadPendingCheckInBookings(); // Refresh the view
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
    AddReservationDialog dialog = new AddReservationDialog(mainFrame);

    dialog.setVisible(true);

    Booking booking = dialog.getBooking();
    if (booking != null) {
      bookingsDAO.insert(booking);
      loadFutureBookings();
    }
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
