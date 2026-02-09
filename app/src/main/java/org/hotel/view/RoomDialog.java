
package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.hotel.model.Room;
import org.hotel.model.RoomType;

public class RoomDialog extends JDialog {

  private final Color BG = Color.decode("#f9fafb");

  private final RoundedTextField roomNoField = new RoundedTextField();
  private final RoundedTextField priceField = new RoundedTextField();
  private final JComboBox<RoomType> typeCombo = new JComboBox<>(RoomType.values());
  private final JCheckBox availableCheck = new JCheckBox("Available");

  private JLabel roomNoErr, typeErr, priceErr;

  private Room result;

  // for duplicate check (optional)
  public interface RoomNumberExists {
    boolean exists(int roomNo);
  }

  private final RoomNumberExists roomNumberExists;
  private final Integer editingOriginalRoomNo; // null = add mode

  public RoomDialog(
      MainFrame parent,
      String title,
      Room existing, // null for Add
      RoomNumberExists roomNumberExists) {

    super(parent, title, true);
    this.roomNumberExists = roomNumberExists;
    this.editingOriginalRoomNo = (existing == null) ? null : existing.getRoomNumber();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());
    getContentPane().setBackground(BG);

    setSize(520, 520);
    setMinimumSize(new Dimension(480, 480));
    setLocationRelativeTo(parent);

    initUI();

    // Fill values if editing
    if (existing != null) {
      roomNoField.setText(String.valueOf(existing.getRoomNumber()));
      priceField.setText(String.valueOf(existing.getPrice()));
      typeCombo.setSelectedItem(existing.getType());
      availableCheck.setSelected(existing.isAvailable());
    } else {
      typeCombo.setSelectedItem(RoomType.SINGLE);
      availableCheck.setSelected(true);
    }

    SwingUtilities.invokeLater(roomNoField::requestFocusInWindow);
  }

  public Room getResult() {
    return result;
  }

  // ---------- UI ----------

  private void initUI() {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(BG);
    header.setBorder(BorderFactory.createEmptyBorder(18, 24, 8, 24));

    JLabel title = new JLabel(getTitle());
    title.setFont(title.getFont().deriveFont(20f));
    title.setForeground(Color.decode("#111827"));

    header.add(title, BorderLayout.WEST);
    add(header, BorderLayout.NORTH);

    JPanel formWrapper = new JPanel(new BorderLayout());
    formWrapper.setBackground(BG);
    formWrapper.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

    JPanel form = new JPanel();
    form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
    form.setBackground(BG);
    form.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

    enforceFieldSize(roomNoField);
    enforceFieldSize(priceField);

    typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    typeCombo.setAlignmentX(LEFT_ALIGNMENT);

    availableCheck.setBackground(BG);
    availableCheck.setAlignmentX(LEFT_ALIGNMENT);

    roomNoErr = createErrorLabel();
    typeErr = createErrorLabel();
    priceErr = createErrorLabel();

    form.add(fieldBlock("Room Number *", roomNoField, roomNoErr));
    form.add(Box.createVerticalStrut(16));

    form.add(fieldBlock("Type *", typeCombo, typeErr));
    form.add(Box.createVerticalStrut(16));

    form.add(fieldBlock("Price *", priceField, priceErr));
    form.add(Box.createVerticalStrut(12));

    form.add(availableCheck);

    addValidateOnBlur(roomNoField);
    addValidateOnBlur(priceField);
    typeCombo.addActionListener(e -> validateForm(false));

    formWrapper.add(form, BorderLayout.CENTER);
    add(formWrapper, BorderLayout.CENTER);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    actions.setBackground(BG);
    actions.setBorder(BorderFactory.createEmptyBorder(10, 24, 18, 24));

    JButton cancelBtn = new JButton("Cancel");
    JButton saveBtn = new JButton("Save");

    cancelBtn.addActionListener(e -> {
      result = null;
      dispose();
    });

    saveBtn.addActionListener(e -> onSave());

    actions.add(cancelBtn);
    actions.add(saveBtn);

    add(actions, BorderLayout.SOUTH);
  }

  private void onSave() {
    if (!validateForm(true))
      return;

    int roomNo = Integer.parseInt(roomNoField.getText().trim());
    double price = Double.parseDouble(priceField.getText().trim());
    RoomType type = (RoomType) typeCombo.getSelectedItem();
    boolean available = availableCheck.isSelected();

    // id=0 for new. If you want to preserve id for edit, do that in controller
    // (easy).
    result = new Room(0, roomNo, type, price, available);
    dispose();
  }

  // ---------- validation ----------

  private void clearErrors() {
    roomNoField.setError(false);
    priceField.setError(false);

    setError(roomNoErr, " ");
    setError(typeErr, " ");
    setError(priceErr, " ");
  }

  private void setError(JLabel lbl, String msg) {
    lbl.setText((msg == null || msg.isBlank()) ? " " : msg);
  }

  private void mark(RoundedTextField field, JLabel err, boolean bad, String msg) {
    field.setError(bad);
    setError(err, bad ? msg : " ");
  }

  private boolean validateForm(boolean focusFirstBad) {
    clearErrors();
    boolean ok = true;
    JComponent firstBad = null;

    String roomNoText = roomNoField.getText().trim();
    if (roomNoText.isEmpty()) {
      mark(roomNoField, roomNoErr, true, "Room number is required.");
      ok = false;
      firstBad = firstBad == null ? roomNoField : firstBad;
    } else {
      try {
        int roomNo = Integer.parseInt(roomNoText);
        if (roomNo <= 0)
          throw new NumberFormatException();

        // duplicate check (ignore if unchanged during edit)
        if (roomNumberExists != null) {
          boolean changed = (editingOriginalRoomNo == null) || (roomNo != editingOriginalRoomNo);
          if (changed && roomNumberExists.exists(roomNo)) {
            mark(roomNoField, roomNoErr, true, "Room " + roomNo + " already exists.");
            ok = false;
            firstBad = firstBad == null ? roomNoField : firstBad;
          }
        }

      } catch (NumberFormatException e) {
        mark(roomNoField, roomNoErr, true, "Use a positive whole number.");
        ok = false;
        firstBad = firstBad == null ? roomNoField : firstBad;
      }
    }

    RoomType type = (RoomType) typeCombo.getSelectedItem();
    if (type == null) {
      setError(typeErr, "Please select a room type.");
      ok = false;
      firstBad = firstBad == null ? typeCombo : firstBad;
    }

    String priceText = priceField.getText().trim();
    if (priceText.isEmpty()) {
      mark(priceField, priceErr, true, "Price is required.");
      ok = false;
      firstBad = firstBad == null ? priceField : firstBad;
    } else {
      try {
        double price = Double.parseDouble(priceText);
        if (price <= 0)
          throw new NumberFormatException();
      } catch (NumberFormatException e) {
        mark(priceField, priceErr, true, "Price must be greater than 0.");
        ok = false;
        firstBad = firstBad == null ? priceField : firstBad;
      }
    }

    if (!ok && focusFirstBad && firstBad != null) {
      firstBad.requestFocusInWindow();
    }

    return ok;
  }

  // ---------- small UI helpers (same style as your reservation dialog)
  // ----------

  private JLabel createErrorLabel() {
    JLabel lbl = new JLabel(" ");
    lbl.setForeground(Color.decode("#ef4444"));
    lbl.setFont(lbl.getFont().deriveFont(12f));
    lbl.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 2));
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    return lbl;
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
}
