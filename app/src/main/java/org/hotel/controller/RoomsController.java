package org.hotel.controller;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.hotel.model.Room;
import org.hotel.model.dao.RoomDAO;
import org.hotel.view.MainFrame;
import org.hotel.view.RoomsView;
import org.hotel.view.RoundedTextField;

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
    mainFrame.getRoomsBtn().addActionListener(e -> loadRooms());
  }

  public void loadRooms() {
    List<Room> rooms = roomDAO.getAll();
    roomsView = new RoomsView(rooms);

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
    Room newRoom = showAddDialog();

    if (newRoom != null) {
      roomDAO.insert(newRoom);
      loadRooms();
    }

  }

  private Room showAddDialog() {
    RoundedTextField roomNoField = new RoundedTextField();
    RoundedTextField typeField = new RoundedTextField();
    RoundedTextField priceField = new RoundedTextField();

    JCheckBox availableCheck = new JCheckBox("Available");
    availableCheck.setSelected(true);

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Room Number:"));
    panel.add(roomNoField);
    panel.add(new JLabel("Type:"));
    panel.add(typeField);
    panel.add(new JLabel("Price:"));
    panel.add(priceField);
    panel.add(availableCheck);

    int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Add new Room", JOptionPane.OK_CANCEL_OPTION);

    if (result != JOptionPane.OK_OPTION)
      return null;

    return new Room(
        0,
        Integer.parseInt(roomNoField.getText()),
        typeField.getText(),
        Double.parseDouble(priceField.getText()),
        availableCheck.isSelected());
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
    RoundedTextField roomNoField = new RoundedTextField(String.valueOf(room.getRoomNumber()));
    RoundedTextField typeField = new RoundedTextField(room.getType());
    RoundedTextField priceField = new RoundedTextField(String.valueOf(room.getPrice()));

    JCheckBox availableCheck = new JCheckBox("Available");
    availableCheck.setSelected(room.isAvailable());

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Room Number: "));
    panel.add(roomNoField);
    panel.add(new JLabel("Type: "));
    panel.add(typeField);
    panel.add(new JLabel("Price: "));
    panel.add(priceField);
    panel.add(availableCheck);

    int result = JOptionPane.showConfirmDialog(
        mainFrame, panel, "Edit Room", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
      room.setRoomNumber(Integer.parseInt(roomNoField.getText()));
      room.setType(typeField.getText());
      room.setPrice(Double.parseDouble(priceField.getText()));
      room.setAvailable(availableCheck.isSelected());

      roomDAO.update(room);
      loadRooms();
    }
  }
}
