package org.hotel.controller;

import org.hotel.model.Room;
import org.hotel.model.dao.RoomDAO;
import org.hotel.view.MainFrame;
import org.hotel.view.RoomsView;

import java.util.List;
import java.awt.event.ActionListener;

public class RoomsController {
  private MainFrame mainFrame;
  private RoomDAO roomDAO;

  public RoomsController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.roomDAO = new RoomDAO();

    registerEvents();
  }

  private void registerEvents() {
    mainFrame.getRoomsBtn().addActionListener(e -> loadRooms());
  }

  public void loadRooms() {
    List<Room> rooms = roomDAO.getAll();
    RoomsView roomsView = new RoomsView(rooms);

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(roomsView, "Rooms");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Rooms");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }
}
