package org.hotel.controller;

import org.hotel.model.dao.DashboardDAO;
import org.hotel.view.DashboardView;
import org.hotel.view.MainFrame;

public class DashboardController {

  private DashboardDAO dashboardDAO;
  private DashboardView dashboardView;
  private MainFrame mainFrame;

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
    dashboardView.setCheckedIn(dashboardDAO.getCheckedInCount());
    dashboardView.setReserved(dashboardDAO.getReservationCount());
    dashboardView.setTodayCheckIn(dashboardDAO.getTodayCheckInCount());
    dashboardView.setTodayCheckOut(dashboardDAO.getTodayCheckOutCount());

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
