package org.hotel.controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.hotel.model.Room;
import org.hotel.model.dao.RoomDAO;
import org.hotel.view.MainFrame;
import org.hotel.view.RoomDialog;
import org.hotel.view.RoomsView;

public class RoomsController {
  private MainFrame mainFrame;
  private RoomDAO roomDAO;
  private RoomsView roomsView;

  public RoomsController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.roomDAO = new RoomDAO();

    registerEvents();
  }

  private void registerEvents() {
    if (mainFrame.getRoomsBtn() != null) {
      mainFrame.getRoomsBtn().addActionListener(e -> loadRooms());
    }
  }

  public void loadRooms() {
    List<Room> rooms = roomDAO.getAll();
    roomsView = new RoomsView(rooms);

    roomsView.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
      private void changed() {
        String q = roomsView.getSearchField().getText().trim();
        List<Room> results = q.isBlank() ? roomDAO.getAll() : roomDAO.searchByRoomNumber(q);
        roomsView.setRooms(results);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        changed();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        changed();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        changed();
      }
    });

    roomsView.getEditBtn().addActionListener(e -> onEditRoom());
    roomsView.getDeleteBtn().addActionListener(e -> onDeleteRoom());
    roomsView.getAddBtn().addActionListener(e -> onAddRoom());

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(roomsView, "Rooms");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Rooms");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }

  private void onAddRoom() {
    RoomDialog dialog = new RoomDialog(
        mainFrame,
        "Add Room",
        null,
        roomNo -> roomDAO.getByRoomNumber(roomNo) != null);

    dialog.setVisible(true);
    Room newRoom = dialog.getResult();

    if (newRoom != null) {
      roomDAO.insert(newRoom);
      loadRooms();
    }
  }

  private void onDeleteRoom() {
    int selectedRow = roomsView.getRoomTable().getSelectedRow();

    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(mainFrame, "Please select a room to delete");
      return;
    }

    int id = (int) roomsView.getTableModel().getValueAt(selectedRow, 1);
    Room room = roomDAO.getById(id);

    int confirm = JOptionPane.showConfirmDialog(null,
        "Are you sure you want to delete Room " + room.getRoomNumber() + "?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      roomDAO.delete(room.getId());
      loadRooms();
    }
  }

  private void onEditRoom() {
    int selectedRow = roomsView.getRoomTable().getSelectedRow();

    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(mainFrame, "Please select a room to edit.");
      return;
    }

    int roomNumber = (int) roomsView.getTableModel().getValueAt(selectedRow, 2);
    Room room = roomDAO.getByRoomNumber(roomNumber);

    if (room == null) {
      JOptionPane.showMessageDialog(mainFrame, "Error: Room not found.");
      return;
    }

    showEditDialog(room);
  }

  private void showEditDialog(Room room) {
    RoomDialog dialog = new RoomDialog(
        mainFrame,
        "Edit Room",
        room,
        roomNo -> roomDAO.getByRoomNumber(roomNo) != null);

    dialog.setVisible(true);
    Room updated = dialog.getResult();

    if (updated != null) {
      // preserve the real DB id when editing
      updated.setId(room.getId());
      roomDAO.update(updated);
      loadRooms();
    }
  }
}
