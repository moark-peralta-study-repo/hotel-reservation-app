package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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

  private final Color BG = Color.decode("#f9fafb");
  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

  private RoundedTextField customerNameField;
  private RoundedTextField phoneField;
  private RoundedTextField emailField;

  private JComboBox<Room> roomCombo;
  private JDatePickerImpl checkInPicker;
  private JDatePickerImpl checkOutPicker;

  private JLabel totalPriceLbl;

  private final Booking booking;
  private final BookingsDAO bookingsDAO;
  private final CustomerDAO customerDAO = new CustomerDAO();
  private final RoomDAO roomDAO = new RoomDAO();

  public EditReservationDialog(MainFrame parent, Booking booking, BookingsDAO dao) {
    super(parent, "Edit Reservation", true);
    this.booking = booking;
    this.bookingsDAO = dao;

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setSize(560, 700);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout());
    getContentPane().setBackground(BG);

    initUI();
    loadBookingData();
  }

  private void initUI() {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(BG);
    header.setBorder(BorderFactory.createEmptyBorder(18, 24, 8, 24));

    JLabel title = new HeaderLabel("Edit Reservation");
    header.add(title, BorderLayout.WEST);

    add(header, BorderLayout.NORTH);

    JPanel formWrapper = new JPanel(new BorderLayout());
    formWrapper.setBackground(BG);
    formWrapper.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

    JPanel form = new JPanel();
    form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
    form.setBackground(BG);
    form.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

    customerNameField = new RoundedTextField();
    phoneField = new RoundedTextField();
    emailField = new RoundedTextField();

    enforceFieldSize(customerNameField);
    enforceFieldSize(phoneField);
    enforceFieldSize(emailField);

    roomCombo = new JComboBox<>(roomDAO.getAll().toArray(new Room[0]));
    roomCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    checkInPicker = createDatePicker();
    checkOutPicker = createDatePicker();

    totalPriceLbl = new JLabel("₱0.00");
    totalPriceLbl.setFont(totalPriceLbl.getFont().deriveFont(16f));

    form.add(fieldBlock("Customer Name", customerNameField));
    form.add(Box.createVerticalStrut(16));

    form.add(fieldBlock("Phone", phoneField));
    form.add(Box.createVerticalStrut(16));

    form.add(fieldBlock("Email", emailField));
    form.add(Box.createVerticalStrut(24));

    form.add(fieldBlock("Room", roomCombo));
    form.add(Box.createVerticalStrut(24));

    form.add(fieldBlock("Check-in Date", padded(checkInPicker)));
    form.add(Box.createVerticalStrut(16));

    form.add(fieldBlock("Check-out Date", padded(checkOutPicker)));
    form.add(Box.createVerticalStrut(24));

    form.add(createFieldLabel("Total Price"));
    form.add(Box.createVerticalStrut(8));
    form.add(totalBlock());
    form.add(Box.createVerticalStrut(24));

    roomCombo.addActionListener(e -> updateTotal());
    checkInPicker.getModel().addChangeListener(e -> updateTotal());
    checkOutPicker.getModel().addChangeListener(e -> updateTotal());

    formWrapper.add(form, BorderLayout.CENTER);
    add(formWrapper, BorderLayout.CENTER);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    actions.setBackground(BG);
    actions.setBorder(BorderFactory.createEmptyBorder(10, 24, 18, 24));

    JButton cancelBtn = new JButton("Cancel");
    JButton saveBtn = new JButton("Save");

    cancelBtn.addActionListener(e -> dispose());
    saveBtn.addActionListener(e -> onSave());

    actions.add(cancelBtn);
    actions.add(saveBtn);

    add(actions, BorderLayout.SOUTH);
  }

  private void enforceFieldSize(JComponent c) {
    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    c.setAlignmentX(LEFT_ALIGNMENT);
  }

  private JLabel createFieldLabel(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setForeground(Color.decode("#374151"));
    lbl.setBorder(BorderFactory.createEmptyBorder(2, 2, 6, 2));
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    return lbl;
  }

  private JPanel fieldBlock(String labelText, java.awt.Component field) {
    JPanel block = new JPanel();
    block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
    block.setBackground(BG);
    block.setAlignmentX(LEFT_ALIGNMENT);

    block.add(createFieldLabel(labelText));
    block.add(field);

    return block;
  }

  private JComponent padded(java.awt.Component c) {
    JPanel wrap = new JPanel(new BorderLayout());
    wrap.setBackground(BG);
    wrap.setAlignmentX(LEFT_ALIGNMENT);
    wrap.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

    if (c instanceof JComponent jc) {
      jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    wrap.add(c, BorderLayout.CENTER);
    return wrap;
  }

  private JComponent totalBlock() {
    JPanel totalWrap = new JPanel(new BorderLayout());
    totalWrap.setBackground(Color.decode("#ffffff"));
    totalWrap.setAlignmentX(LEFT_ALIGNMENT);

    totalWrap.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.decode("#e5e7eb")),
        BorderFactory.createEmptyBorder(16, 18, 16, 18)));

    totalWrap.add(totalPriceLbl, BorderLayout.WEST);
    return totalWrap;
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
    Customer c = customerDAO.getById(booking.getCustomerId());
    if (c != null) {
      customerNameField.setText(c.getName());
      phoneField.setText(c.getPhone() == null ? "" : c.getPhone());
      emailField.setText(c.getEmail() == null ? "" : c.getEmail());
    }

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
      totalPriceLbl.setText("₱0.00");
      return;
    }

    LocalDate inDate = in.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate outDate = out.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    long nights = ChronoUnit.DAYS.between(inDate, outDate);

    if (nights <= 0) {
      totalPriceLbl.setText("₱0.00");
      return;
    }

    double total = nights * room.getPrice();
    totalPriceLbl.setText(String.format("₱%.2f", total));
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

    Customer c = customerDAO.getById(booking.getCustomerId());
    if (c != null) {
      c.setName(name);
      c.setPhone(phoneField.getText().trim());
      c.setEmail(emailField.getText().trim());
      customerDAO.update(c); // make sure this exists
    }

    LocalDate inLocal = inDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate outLocal = outDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    long nights = ChronoUnit.DAYS.between(inLocal, outLocal);
    double totalPrice = nights * room.getPrice();

    booking.setRoomId(room.getId());
    booking.setCheckIn(SDF.format(inDate));
    booking.setCheckOut(SDF.format(outDate));
    booking.setTotalPrice(totalPrice);

    if (booking.getStatus() == null) {
      booking.setStatus(BookingStatus.RESERVED);
    }

    bookingsDAO.update(booking);
    dispose();
  }

  public Booking getBooking() {
    return booking;
  }
}
