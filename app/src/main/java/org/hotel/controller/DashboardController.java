package org.hotel.controller;

import org.hotel.model.dao.DashboardDAO;
import org.hotel.view.DashboardView;
import org.hotel.view.MainFrame;

public class DashboardController {

  private final DashboardDAO dashboardDAO;
  private final DashboardView dashboardView;
  private final MainFrame mainFrame;

  public DashboardController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.dashboardDAO = new DashboardDAO();
    this.dashboardView = new DashboardView();

    registerEvents();
  }

  private void registerEvents() {
    mainFrame.getDashboardBtn().addActionListener(e -> loadData());
  }

  private void loadData() {
    int totalRooms = dashboardDAO.getTotalRoomsCount();
    int occupied = dashboardDAO.getOccupiedRoomsCount();

    int available = dashboardDAO.getAvailableRoomsCount();
    // fallback if is_available isn’t being maintained yet
    if (available <= 0 && totalRooms > 0) {
      available = Math.max(0, totalRooms - occupied);
    }

    dashboardView.setOccupancy(occupied, totalRooms);
    dashboardView.setRoomsAvailable(available, totalRooms);

    dashboardView.setArrivalsToday(dashboardDAO.getArrivalsTodayCount());
    dashboardView.setDeparturesToday(dashboardDAO.getDeparturesTodayCount());

    dashboardView.setTodayEarnings(dashboardDAO.getTodayEarnings());
    dashboardView.setMonthEarnings(dashboardDAO.getMonthToDateEarnings());

    dashboardView.setArrivalsRows(dashboardDAO.getArrivalsTodayRows());
    dashboardView.setCheckoutRows(dashboardDAO.getDeparturesTodayRows());

    show();
  }

  public void show() {
    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(dashboardView, "Dashboard");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Dashboard");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }
}
