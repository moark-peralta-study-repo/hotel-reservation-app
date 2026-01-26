package org.hotel.controller;

import java.util.List;

import org.hotel.model.User;
import org.hotel.model.dao.UsersDAO;
import org.hotel.view.MainFrame;
import org.hotel.view.ShowAddUserDialog;
import org.hotel.view.UsersView;

public class UserController {
  private MainFrame mainFrame;
  private UsersDAO usersDAO;
  private UsersView usersView;

  public UserController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.usersDAO = new UsersDAO();

    registerEvents();
  }

  private void registerEvents() {
    mainFrame.getUsersBtn().addActionListener(e -> loadUsers());
  }

  private void onAddUser() {
    new ShowAddUserDialog(mainFrame);
  }

  private void loadUsers() {
    List<User> users = usersDAO.getAll();

    usersView = new UsersView(users);
    usersView.getAddUserBtn().addActionListener(e -> onAddUser());

    mainFrame.getContentPanel().removeAll();
    mainFrame.getContentPanel().add(usersView, "Users");
    mainFrame.getCardLayout().show(mainFrame.getContentPanel(), "Users");
    mainFrame.getContentPanel().revalidate();
    mainFrame.getContentPanel().repaint();
  }
}
