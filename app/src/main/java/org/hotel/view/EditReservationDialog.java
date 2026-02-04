package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import raven.datetime.DateSelectionAble;
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

  private JLabel nameErr, phoneErr, emailErr, roomErr, datesErr;

  private final Booking booking;
  private final BookingsDAO bookingsDAO;
  private final CustomerDAO customerDAO = new CustomerDAO();
  private final RoomDAO roomDAO = new RoomDAO();

  private final Map<Integer, List<DateRange>> bookedRangesByRoom = new HashMap<>();
  private DateSelectionAble dateRule;

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

    setupDisabledDates(booking.getId());

    totalPriceLbl = new JLabel("₱0.00");
    totalPriceLbl.setFont(totalPriceLbl.getFont().deriveFont(16f));

    nameErr = createErrorLabel();
    phoneErr = createErrorLabel();
    emailErr = createErrorLabel();
    roomErr = createErrorLabel();
    datesErr = createErrorLabel();

    form.add(fieldBlock("Customer Name", customerNameField, nameErr));
    form.add(Box.createVerticalStrut(16));

    form.add(fieldBlock("Phone *", phoneField, phoneErr));
    form.add(Box.createVerticalStrut(16));

    form.add(fieldBlock("Email *", emailField, emailErr));
    form.add(Box.createVerticalStrut(24));

    form.add(fieldBlock("Room", roomCombo, roomErr));
    form.add(Box.createVerticalStrut(24));

    form.add(fieldBlock("Stay Dates", padded(stayEditor), datesErr));
    form.add(Box.createVerticalStrut(24));

    form.add(createFieldLabel("Total Price"));
    form.add(Box.createVerticalStrut(8));
    form.add(totalBlock());
    form.add(Box.createVerticalStrut(24));

    roomCombo.addActionListener(e -> {
      refreshDateRuleAndClearIfInvalid();
      updateTotal();
      validateForm(false);
    });

    stayPicker.addDateSelectionListener((DateSelectionListener) e -> {
      Room room = (Room) roomCombo.getSelectedItem();
      LocalDate[] range = stayPicker.getSelectedDateRange();

      if (range != null && range.length >= 2 && range[0] != null && range[1] != null) {
        LocalDate start = range[0];
        LocalDate end = range[1];

        if (start.isBefore(LocalDate.now())) {
          setError(datesErr, "Check-in cannot be in the past.");
          stayPicker.clearSelectedDate();
        } else if (rangeOverlapsBooked(room, start, end)) {
          setError(datesErr, "Selected stay overlaps reserved/checked-in dates.");
          stayPicker.clearSelectedDate();
        } else {
          setError(datesErr, " ");
        }
      }

      updateTotal();
      validateForm(false);
    });

    addValidateOnBlur(customerNameField);
    addValidateOnBlur(phoneField);
    addValidateOnBlur(emailField);

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

  private static class DateRange {
    final LocalDate from;
    final LocalDate to;

    DateRange(LocalDate from, LocalDate to) {
      this.from = from;
      this.to = to;
    }

    boolean contains(LocalDate d) {
      return (d.equals(from) || d.isAfter(from)) && d.isBefore(to);
    }
  }

  private void setupDisabledDates(Integer ignoreBookingId) {
    loadBookedRanges(ignoreBookingId);

    dateRule = (LocalDate date) -> {
      if (date.isBefore(LocalDate.now()))
        return false;

      Room room = (Room) roomCombo.getSelectedItem();
      if (room == null)
        return true;

      List<DateRange> ranges = bookedRangesByRoom.get(room.getId());
      if (ranges == null)
        return true;

      for (DateRange r : ranges) {
        if (r.contains(date))
          return false;
      }
      return true;
    };

    stayPicker.setDateSelectionAble(dateRule);
  }

  private void loadBookedRanges(Integer ignoreBookingId) {
    bookedRangesByRoom.clear();

    List<Booking> all = bookingsDAO.getAll();

    for (Booking b : all) {
      if (ignoreBookingId != null && b.getId() == ignoreBookingId)
        continue;

      if (b.getStatus() != BookingStatus.RESERVED && b.getStatus() != BookingStatus.CHECKED_IN)
        continue;

      LocalDate in = LocalDate.parse(b.getCheckIn());
      LocalDate out = LocalDate.parse(b.getCheckOut());
      if (!in.isBefore(out))
        continue;

      bookedRangesByRoom
          .computeIfAbsent(b.getRoomId(), k -> new ArrayList<>())
          .add(new DateRange(in, out));
    }
  }

  private void refreshDateRuleAndClearIfInvalid() {
    stayPicker.setDateSelectionAble(dateRule);

    LocalDate[] range = stayPicker.getSelectedDateRange();
    if (range != null && range.length >= 2 && range[0] != null && range[1] != null) {
      if (!dateRule.isDateSelectedAble(range[0]) || !dateRule.isDateSelectedAble(range[1])) {
        stayPicker.clearSelectedDate();
      }
    }
  }

  private JLabel createErrorLabel() {
    JLabel lbl = new JLabel(" ");
    lbl.setForeground(Color.decode("#ef4444"));
    lbl.setFont(lbl.getFont().deriveFont(12f));
    lbl.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 2));
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    return lbl;
  }

  private void setError(JLabel lbl, String msg) {
    lbl.setText((msg == null || msg.isBlank()) ? " " : msg);
  }

  private void mark(RoundedTextField field, JLabel err, boolean bad, String msg) {
    field.setError(bad);
    setError(err, bad ? msg : " ");
  }

  private void clearErrors() {
    customerNameField.setError(false);
    phoneField.setError(false);
    emailField.setError(false);

    setError(nameErr, " ");
    setError(phoneErr, " ");
    setError(emailErr, " ");
    setError(roomErr, " ");
    setError(datesErr, " ");
  }

  private boolean isValidEmail(String email) {
    return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
  }

  private boolean isValidPhone(String phone) {
    return phone != null && phone.matches("^(09\\d{9}|\\+63\\d{10})$");
  }

  private boolean validateForm(boolean showFocus) {
    clearErrors();
    boolean ok = true;

    String name = customerNameField.getText().trim();
    String phone = phoneField.getText().trim();
    String email = emailField.getText().trim();
    Room room = (Room) roomCombo.getSelectedItem();
    LocalDate[] range = stayPicker.getSelectedDateRange();

    JComponent firstBad = null;

    if (name.isEmpty()) {
      mark(customerNameField, nameErr, true, "Customer name is required.");
      ok = false;
      if (firstBad == null)
        firstBad = customerNameField;
    } else {
      mark(customerNameField, nameErr, false, null);
    }

    if (phone.isEmpty()) {
      mark(phoneField, phoneErr, true, "Phone number is required.");
      ok = false;
      if (firstBad == null)
        firstBad = phoneField;
    } else if (!isValidPhone(phone)) {
      mark(phoneField, phoneErr, true, "Use 09XXXXXXXXX or +63XXXXXXXXXX.");
      ok = false;
      if (firstBad == null)
        firstBad = phoneField;
    } else {
      mark(phoneField, phoneErr, false, null);
    }

    if (email.isEmpty()) {
      mark(emailField, emailErr, true, "Email address is required.");
      ok = false;
      if (firstBad == null)
        firstBad = emailField;
    } else if (!isValidEmail(email)) {
      mark(emailField, emailErr, true, "Invalid email format.");
      ok = false;
      if (firstBad == null)
        firstBad = emailField;
    } else {
      mark(emailField, emailErr, false, null);
    }

    if (room == null) {
      setError(roomErr, "Please select a room.");
      ok = false;
      if (firstBad == null)
        firstBad = (JComponent) roomCombo;
    } else {
      setError(roomErr, " ");
    }

    if (range == null || range.length < 2 || range[0] == null || range[1] == null) {
      setError(datesErr, "Please select check-in and check-out dates.");
      ok = false;
      if (firstBad == null)
        firstBad = stayEditor;
    } else if (!range[0].isBefore(range[1])) {
      setError(datesErr, "Check-in must be before check-out.");
      ok = false;
      if (firstBad == null)
        firstBad = stayEditor;
    } else if (range[0].isBefore(LocalDate.now())) {
      setError(datesErr, "Check-in cannot be in the past.");
      ok = false;
      if (firstBad == null)
        firstBad = stayEditor;
    } else if (rangeOverlapsBooked(room, range[0], range[1])) {
      setError(datesErr, "Selected stay overlaps reserved/checked-in dates.");
      ok = false;
      if (firstBad == null)
        firstBad = stayEditor;
    } else {
      if (dateRule != null && (!dateRule.isDateSelectedAble(range[0]) || !dateRule.isDateSelectedAble(range[1]))) {
        setError(datesErr, "Selected dates include unavailable days.");
        ok = false;
        if (firstBad == null)
          firstBad = stayEditor;
      } else {
        setError(datesErr, " ");
      }
    }

    if (!ok && showFocus && firstBad != null) {
      firstBad.requestFocusInWindow();
    }

    return ok;
  }

  private void addValidateOnBlur(JComponent c) {
    c.addFocusListener(new java.awt.event.FocusAdapter() {
      @Override
      public void focusLost(java.awt.event.FocusEvent e) {
        validateForm(false);
      }
    });
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

  private JPanel fieldBlock(String labelText, java.awt.Component field, JLabel errLabel) {
    JPanel block = new JPanel();
    block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
    block.setBackground(BG);
    block.setAlignmentX(LEFT_ALIGNMENT);

    block.add(createFieldLabel(labelText));
    block.add(field);
    if (errLabel != null)
      block.add(errLabel);

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

    refreshDateRuleAndClearIfInvalid();
    updateTotal();
    validateForm(false);
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
    if (!validateForm(true))
      return;

    String name = customerNameField.getText().trim();
    String phone = phoneField.getText().trim();
    String email = emailField.getText().trim();
    Room room = (Room) roomCombo.getSelectedItem();

    LocalDate[] range = stayPicker.getSelectedDateRange();
    LocalDate checkIn = range[0];
    LocalDate checkOut = range[1];

    Customer c = customerDAO.getById(booking.getCustomerId());
    if (c != null) {
      c.setName(name);
      c.setPhone(phone);
      c.setEmail(email);
      try {
        customerDAO.update(c);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Failed to update customer.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
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

    try {
      bookingsDAO.update(booking);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Failed to update booking.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    dispose();
  }

  private boolean rangeOverlapsBooked(Room room, LocalDate start, LocalDate end) {
    if (room == null || start == null || end == null)
      return false;
    if (!start.isBefore(end))
      return true;

    List<DateRange> ranges = bookedRangesByRoom.get(room.getId());
    if (ranges == null)
      return false;

    for (DateRange r : ranges) {
      boolean overlaps = start.isBefore(r.to) && end.isAfter(r.from);
      if (overlaps)
        return true;
    }
    return false;
  }

  public Booking getBooking() {
    return booking;
  }
}
