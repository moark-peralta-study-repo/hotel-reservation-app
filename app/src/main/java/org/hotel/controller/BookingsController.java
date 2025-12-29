package org.hotel.controller;

import java.util.List;

import org.hotel.model.Booking;
import org.hotel.model.dao.BookingsDAO;
import org.hotel.view.BookingsView;
import org.hotel.view.MainFrame;

public class BookingsController {
  private MainFrame mainFrame;
  private BookingsDAO bookingsDAO;
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
  }

  private void loadCheckedInBookings() {
    List<Booking> checkedInBookings = bookingsDAO.getCheckedInBookings();
    bookingsView = new BookingsView(checkedInBookings);

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void loadPendingCheckInBookings() {
    List<Booking> pendingCheckin = bookingsDAO.getPendingCheckIn();
    bookingsView = new BookingsView(pendingCheckin);

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void loadBookings() {
    List<Booking> bookings = bookingsDAO.getAll();
    bookingsView = new BookingsView(bookings);

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(bookingsView, "Bookings");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Bookings");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }
}
