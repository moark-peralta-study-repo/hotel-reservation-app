package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.ParseException;
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
import org.hotel.model.dao.BookingsDAO;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class EditReservationDialog extends JDialog {

  private JTextField customerIdField;
  private JTextField roomIdField;
  private JTextField totalPriceField;

  private JDatePickerImpl checkInPicker;
  private JDatePickerImpl checkOutPicker;

  private Booking booking;
  private BookingsDAO bookingsDAO;

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public EditReservationDialog(MainFrame parent, Booking booking, BookingsDAO dao) {
    super(parent, "Edit Reservation", true);
    this.booking = booking;
    this.bookingsDAO = dao;

    setSize(400, 300);
    setLocationRelativeTo(parent);

    initUI();
    loadBookingData();
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

  private void loadBookingData() {
    customerIdField.setText(String.valueOf(booking.getCustomerId()));
    roomIdField.setText(String.valueOf(booking.getRoomId()));
    totalPriceField.setText(String.valueOf(booking.getTotalPrice()));

    try {
      Date checkInDate = DATE_FORMAT.parse(booking.getCheckIn());
      checkInPicker.getModel().setDate(
          checkInDate.getYear() + 1900, checkInDate.getMonth(), checkInDate.getDate());
      checkInPicker.getModel().setSelected(true);

      Date checkOutDate = DATE_FORMAT.parse(booking.getCheckOut());
      checkOutPicker.getModel().setDate(
          checkOutDate.getYear() + 1900, checkOutDate.getMonth(), checkOutDate.getDate());
      checkOutPicker.getModel().setSelected(true);
    } catch (ParseException e) {
      e.printStackTrace();
    }
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
      Date outDate = (Date) checkOutPicker.getModel().getValue();

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String checkIn = sdf.format(inDate);
      String checkOut = sdf.format(outDate);

      if (!inDate.before(outDate)) {
        JOptionPane.showMessageDialog(this, "Check-in must be before the check-out date.");
        return;
      }

      booking.setCustomerId(customerId);
      booking.setRoomId(roomId);
      booking.setTotalPrice(totalPrice);
      booking.setCheckIn(DATE_FORMAT.format(checkIn));
      booking.setCheckOut(DATE_FORMAT.format(checkOut));

      bookingsDAO.update(booking); // make sure update() exists in your DAO
      dispose();
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid input values.");
    }
  }

  public Booking getBooking() {
    return booking;
  }
}
