package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.hotel.controller.BookingsController;
import org.hotel.controller.DashboardController;
import org.hotel.controller.RoomsController;
import org.hotel.controller.UserController;
import org.hotel.model.User;
import org.hotel.model.dao.UsersDAO;

public class LoginFrame extends JFrame {
  private final LoginWrapper wrapper;
  private final UsersDAO UsersDAO = new UsersDAO();

  public LoginFrame() {
    setTitle("Login");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 600);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    setResizable(false);

    wrapper = new LoginWrapper();
    setContentPane(wrapper);

    getRootPane().setDefaultButton(wrapper.getLoginBtn());

    wrapper.getLoginBtn().addActionListener(e -> validateLogin());
    wrapper.getExitBtn().addActionListener(e -> handleExit());

    setVisible(true);
  }

  private void handleExit() {
    int choice = JOptionPane.showConfirmDialog(wrapper,
        "Are you sure you want to exit?",
        "Confirm Exit",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (choice == JOptionPane.YES_OPTION) {
      Window window = javax.swing.SwingUtilities.getWindowAncestor(wrapper);

      if (window != null) {
        window.dispose();
      }
    }

  }

  private void openMainApp() {
    MainFrame mainFrame = new MainFrame();
    new RoomsController(mainFrame);
    new BookingsController(mainFrame);
    new DashboardController(mainFrame);
    new UserController(mainFrame);
  }

  private void validateLogin() {
    wrapper.getUsernameField().setError(false);
    wrapper.getPasswordField().setError(false);

    String username = wrapper.getUsername();
    char[] password = wrapper.getPassword();

    StringBuilder errors = new StringBuilder();

    if (username.length() == 0) {
      wrapper.getUsernameField().setError(true);
      errors.append("• Field Required\n");
    }

    if (password.length < 6) {
      wrapper.getPasswordField().setError(true);
      errors.append("• Field Required\n");
    }

    if (errors.length() > 0) {
      JOptionPane.showMessageDialog(this, errors.toString(), "Validation Errors", JOptionPane.ERROR_MESSAGE);
      return;
    }

    User user = UsersDAO.login(username, password);

    if (user == null) {
      wrapper.getUsernameField().setError(true);
      wrapper.getPasswordField().setError(true);
      JOptionPane.showMessageDialog(
          this,
          "Invalid username or password",
          "Login Failed",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    dispose();
    openMainApp();
  }
}
