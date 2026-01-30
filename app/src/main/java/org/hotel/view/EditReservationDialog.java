package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;
import org.hotel.model.Customer;
import org.hotel.model.Room;
import org.hotel.model.dao.BookingsDAO;
import org.hotel.model.dao.CustomerDAO;
import org.hotel.model.dao.RoomDAO;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class EditReservationDialog extends JDialog {

  private RoundedTextField customerNameField;
  private RoundedTextField phoneField;
  private RoundedTextField emailField;

  private JComboBox<Room> roomCombo;
  private JLabel totalPriceLbl;

  private JDatePickerImpl checkInPicker;
  private JDatePickerImpl checkOutPicker;

  private final Booking booking;
  private final BookingsDAO bookingsDAO;
  private final CustomerDAO customerDAO = new CustomerDAO();
  private final RoomDAO roomDAO = new RoomDAO();

  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

  public EditReservationDialog(MainFrame parent, Booking booking, BookingsDAO dao) {
    super(parent, "Edit Reservation", true);
    this.booking = booking;
    this.bookingsDAO = dao;

    setSize(450, 320);
    setLocationRelativeTo(parent);

    initUI();
    loadBookingData();
  }

  private void initUI() {
    JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

    customerNameField = new RoundedTextField();
    phoneField = new RoundedTextField();
    emailField = new RoundedTextField();

    roomCombo = new JComboBox<>(roomDAO.getAll().toArray(new Room[0]));
    totalPriceLbl = new JLabel("0.00");

    checkInPicker = createDatePicker();
    checkOutPicker = createDatePicker();

    form.add(new JLabel("Customer Name"));
    form.add(customerNameField);

    form.add(new JLabel("Phone"));
    form.add(phoneField);

    form.add(new JLabel("Email"));
    form.add(emailField);

    form.add(new JLabel("Room"));
    form.add(roomCombo);

    form.add(new JLabel("Check-in Date"));
    form.add(checkInPicker);

    form.add(new JLabel("Check-out Date"));
    form.add(checkOutPicker);

    form.add(new JLabel("Total Price"));
    form.add(totalPriceLbl);

    roomCombo.addActionListener(e -> updateTotal());
    checkInPicker.getModel().addChangeListener(e -> updateTotal());
    checkOutPicker.getModel().addChangeListener(e -> updateTotal());

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
    // Load customer info
    Customer c = customerDAO.getById(booking.getCustomerId());
    if (c != null) {
      customerNameField.setText(c.getName());
      phoneField.setText(c.getPhone() == null ? "" : c.getPhone());
      emailField.setText(c.getEmail() == null ? "" : c.getEmail());
    }

    // Select room in combo
    for (int i = 0; i < roomCombo.getItemCount(); i++) {
      Room r = roomCombo.getItemAt(i);
      if (r.getId() == booking.getRoomId()) {
        roomCombo.setSelectedIndex(i);
        break;
      }
    }

    try {
      Date in = SDF.parse(booking.getCheckIn());
      checkInPicker.getModel().setDate(in.getYear() + 1900, in.getMonth(), in.getDate());
      checkInPicker.getModel().setSelected(true);

      Date out = SDF.parse(booking.getCheckOut());
      checkOutPicker.getModel().setDate(out.getYear() + 1900, out.getMonth(), out.getDate());
      checkOutPicker.getModel().setSelected(true);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    updateTotal();
  }

  private void updateTotal() {
    Room room = (Room) roomCombo.getSelectedItem();
    Date in = (Date) checkInPicker.getModel().getValue();
    Date out = (Date) checkOutPicker.getModel().getValue();

    if (room == null || in == null || out == null) {
      totalPriceLbl.setText("0.00");
      return;
    }

    LocalDate inDate = in.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate outDate = out.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    long nights = ChronoUnit.DAYS.between(inDate, outDate);

    if (nights <= 0) {
      totalPriceLbl.setText("0.00");
      return;
    }

    double total = nights * room.getPrice();
    totalPriceLbl.setText(String.format("%.2f", total));
  }

  private void onSave() {
    String name = customerNameField.getText().trim();
    if (name.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Customer name is required.");
      return;
    }

    Room room = (Room) roomCombo.getSelectedItem();
    if (room == null) {
      JOptionPane.showMessageDialog(this, "Please select a room.");
      return;
    }

    Date inDate = (Date) checkInPicker.getModel().getValue();
    Date outDate = (Date) checkOutPicker.getModel().getValue();

    if (inDate == null || outDate == null) {
      JOptionPane.showMessageDialog(this, "Please select dates.");
      return;
    }

    if (!inDate.before(outDate)) {
      JOptionPane.showMessageDialog(this, "Check-in must be before check-out.");
      return;
    }

    // Update customer details
    Customer c = customerDAO.getById(booking.getCustomerId());
    if (c != null) {
      c.setName(name);
      c.setPhone(phoneField.getText().trim());
      c.setEmail(emailField.getText().trim());
      customerDAO.update(c); // you'll need to add this method
    }

    // Update booking fields
    LocalDate inLocal = inDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate outLocal = outDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    long nights = ChronoUnit.DAYS.between(inLocal, outLocal);
    double totalPrice = nights * room.getPrice();

    booking.setRoomId(room.getId());
    booking.setCheckIn(SDF.format(inDate));
    booking.setCheckOut(SDF.format(outDate));
    booking.setTotalPrice(totalPrice);

    // keep status as is
    if (booking.getStatus() == null)
      booking.setStatus(BookingStatus.RESERVED);

    bookingsDAO.update(booking);
    dispose();
  }

  public Booking getBooking() {
    return booking;
  }
}
