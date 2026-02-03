package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.hotel.model.Booking;
import org.hotel.model.BookingStatus;
import org.hotel.model.Customer;
import org.hotel.model.Room;
import org.hotel.model.dao.BookingsDAO;
import org.hotel.model.dao.CustomerDAO;
import org.hotel.model.dao.RoomDAO;

import raven.datetime.DatePicker;
import raven.datetime.DatePicker.DateSelectionMode;
import raven.datetime.event.DateSelectionListener;

public class EditReservationDialog extends JDialog {

  private final Color BG = Color.decode("#f9fafb");

  private RoundedTextField customerNameField;
  private RoundedTextField phoneField;
  private RoundedTextField emailField;

  private JComboBox<Room> roomCombo;

  private DatePicker stayPicker;
  private JFormattedTextField stayEditor;

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
    roomCombo.setAlignmentX(LEFT_ALIGNMENT);

    stayEditor = new JFormattedTextField();
    stayPicker = createBetweenDatePicker(stayEditor);

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

    form.add(fieldBlock("Stay Dates", padded(stayEditor)));
    form.add(Box.createVerticalStrut(24));

    form.add(createFieldLabel("Total Price"));
    form.add(Box.createVerticalStrut(8));
    form.add(totalBlock());
    form.add(Box.createVerticalStrut(24));

    roomCombo.addActionListener(e -> updateTotal());
    stayPicker.addDateSelectionListener((DateSelectionListener) e -> updateTotal());

    JPanel formWrapper = new JPanel(new BorderLayout());
    formWrapper.setBackground(BG);
    formWrapper.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
    formWrapper.add(form, BorderLayout.NORTH);

    JScrollPane scrollPane = new JScrollPane(
        formWrapper,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(BG);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    add(scrollPane, BorderLayout.CENTER);

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

  private DatePicker createBetweenDatePicker(JFormattedTextField editor) {
    DatePicker picker = new DatePicker();
    picker.setDateSelectionMode(DateSelectionMode.BETWEEN_DATE_SELECTED);
    picker.setDateFormat("yyyy-MM-dd");
    picker.setSeparator(" to ");
    picker.setCloseAfterSelected(true);

    editor.setEditable(false);
    editor.setFocusable(true);
    editor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    editor.setPreferredSize(new Dimension(0, 42));

    picker.setEditor(editor);
    return picker;
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
      jc.setAlignmentX(LEFT_ALIGNMENT);
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

    LocalDate in = LocalDate.parse(booking.getCheckIn());
    LocalDate out = LocalDate.parse(booking.getCheckOut());

    stayPicker.setSelectedDateRange(in, out);

    updateTotal();
  }

  private void updateTotal() {
    Room room = (Room) roomCombo.getSelectedItem();
    LocalDate[] range = stayPicker.getSelectedDateRange();

    if (room == null || range == null || range.length < 2 || range[0] == null || range[1] == null) {
      totalPriceLbl.setText("₱0.00");
      return;
    }

    long nights = ChronoUnit.DAYS.between(range[0], range[1]);

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

    LocalDate[] range = stayPicker.getSelectedDateRange();
    if (range == null || range.length < 2 || range[0] == null || range[1] == null) {
      JOptionPane.showMessageDialog(this, "Please select stay dates.");
      return;
    }

    LocalDate checkIn = range[0];
    LocalDate checkOut = range[1];

    if (!checkIn.isBefore(checkOut)) {
      JOptionPane.showMessageDialog(this, "Check-in must be before check-out.");
      return;
    }

    Customer c = customerDAO.getById(booking.getCustomerId());
    if (c != null) {
      c.setName(name);
      c.setPhone(phoneField.getText().trim());
      c.setEmail(emailField.getText().trim());
      customerDAO.update(c);
    }

    long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
    double totalPrice = nights * room.getPrice();

    booking.setRoomId(room.getId());
    booking.setCheckIn(checkIn.toString());
    booking.setCheckOut(checkOut.toString());
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
