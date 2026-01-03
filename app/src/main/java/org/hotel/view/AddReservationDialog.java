package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class AddReservationDialog extends JDialog {

  private JTextField customerIdField;
  private JTextField roomIdField;
  private JTextField totalPriceField;

  private JDatePickerImpl checkInPicker;
  private JDatePickerImpl checkOutPicker;

  private Booking booking;

  public AddReservationDialog(MainFrame parent) {
    super(parent, "Add Reservation", true);
    setSize(400, 300);
    setLocationRelativeTo(parent);

    initUI();
  }

  private void initUI() {
    JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

    customerIdField = new JTextField();
    roomIdField = new JTextField();
    totalPriceField = new JTextField();

    checkInPicker = createDatePicker();
    checkOutPicker = createDatePicker();

    form.add(new JLabel("Customer ID"));
    form.add(customerIdField);

    form.add(new JLabel("Room ID"));
    form.add(roomIdField);

    form.add(new JLabel("Check-in Date"));
    form.add(checkInPicker);

    form.add(new JLabel("Check-out Date"));
    form.add(checkOutPicker);

    form.add(new JLabel("Total Price"));
    form.add(totalPriceField);

    JButton saveBtn = new JButton("Save");
    JButton cancelBtn = new JButton("Cancel");

    saveBtn.addActionListener(e -> onSave());
    cancelBtn.addActionListener(e -> dispose());

    JPanel actions = new JPanel();
    actions.add(saveBtn);
    actions.add(cancelBtn);

    add(form, BorderLayout.CENTER);
    add(actions, BorderLayout.SOUTH);
  }

  private JDatePickerImpl createDatePicker() {
    UtilDateModel model = new UtilDateModel();
    Properties props = new Properties();
    props.put("text.today", "Today");
    props.put("text.month", "Month");
    props.put("text.year", "Year");

    JDatePanelImpl panel = new JDatePanelImpl(model, props);
    return new JDatePickerImpl(panel, new DateLabelFormatter());
  }

  private void onSave() {
    try {
      int customerId = Integer.parseInt(customerIdField.getText());
      int roomId = Integer.parseInt(roomIdField.getText());
      double totalPrice = Double.parseDouble(totalPriceField.getText());

      if (checkInPicker.getModel().getValue() == null ||
          checkOutPicker.getModel().getValue() == null) {
        JOptionPane.showMessageDialog(this, "Please Select Dates");
        return;
      }

      Date inDate = (Date) checkInPicker.getModel().getValue();
      Date outDate = (Date) checkInPicker.getModel().getValue();

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String checkIn = sdf.format(inDate);
      String checkOut = sdf.format(outDate);

      if (!inDate.before(outDate)) {
        JOptionPane.showMessageDialog(this, "Check-in must be before the check-out date.");
        return;
      }

      booking = new Booking(
          0,
          customerId,
          roomId,
          checkIn,
          checkOut,
          totalPrice,
          BookingStatus.RESERVED);

      dispose();

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Please enter valid numbers for Customer ID, Room ID, and Total Price");
    }
  }

  public Booking getBooking() {
    return booking;
  }
}
