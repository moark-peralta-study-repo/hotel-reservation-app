package org.hotel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class DashboardView extends JPanel {

  private static final Color BG = Color.decode("#f9fafb");
  private static final Color CARD_BG = Color.WHITE;
  private static final Color BORDER = Color.decode("#e5e7eb");
  private static final Color MUTED = Color.decode("#6b7280");
  private static final Color TEXT = Color.decode("#111827");

  private static final Font TITLE_FONT = new Font("Poppins", Font.BOLD, 22);
  private static final Font KPI_VALUE_FONT = new Font("Roboto", Font.BOLD, 32);
  private static final Font KPI_TITLE_FONT = new Font("Poppins", Font.BOLD, 14);
  private static final Font KPI_SUB_FONT = new Font("Poppins", Font.PLAIN, 12);

  private final KpiCard occupancyCard = new KpiCard("Occupancy Rate", "0%", "0 / 0 occupied");
  private final KpiCard todayRevenueCard = new KpiCard("Today’s Earnings", "₱0.00", "From check-ins / extensions");
  private final KpiCard monthRevenueCard = new KpiCard("This Month", "₱0.00", "Revenue to date");
  private final KpiCard arrivalsTodayCard = new KpiCard("Arrivals Today", "0", "Reserved check-ins today");
  private final KpiCard departuresTodayCard = new KpiCard("Check-outs Today", "0",
      "Currently checked-in leaving today");
  private final KpiCard roomsAvailableCard = new KpiCard("Rooms Available", "0", "Available now");

  private final JProgressBar occupancyBar = new JProgressBar(0, 100);

  private final JTable arrivalsTable;
  private final DefaultTableModel arrivalsModel;

  private final JTable checkoutsTable;
  private final DefaultTableModel checkoutsModel;

  public DashboardView() {
    setLayout(new BorderLayout());
    setBackground(BG);
    setBorder(new EmptyBorder(25, 30, 25, 30));

    add(buildHeader(), BorderLayout.NORTH);

    JPanel content = new JPanel(new BorderLayout(0, 18));
    content.setBackground(BG);
    add(content, BorderLayout.CENTER);

    JPanel kpiGrid = new JPanel(new java.awt.GridLayout(2, 3, 16, 16));
    kpiGrid.setBackground(BG);

    kpiGrid.add(occupancyCard);
    kpiGrid.add(todayRevenueCard);
    kpiGrid.add(monthRevenueCard);
    kpiGrid.add(arrivalsTodayCard);
    kpiGrid.add(departuresTodayCard);
    kpiGrid.add(roomsAvailableCard);

    occupancyCard.addExtra(buildOccupancyProgress(), 10);

    content.add(kpiGrid, BorderLayout.NORTH);

    arrivalsModel = new DefaultTableModel(new Object[] { "Booking ID", "Guest", "Room", "Check-in", "Nights" }, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    arrivalsTable = new JTable(arrivalsModel);
    styleTable(arrivalsTable);

    checkoutsModel = new DefaultTableModel(new Object[] { "Booking ID", "Guest", "Room", "Check-out", "Balance" }, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    checkoutsTable = new JTable(checkoutsModel);
    styleTable(checkoutsTable);

    JPanel activity = buildTwoPanelSection(
        "Today’s Activity",
        "Arrivals (Reserved)",
        new JScrollPane(arrivalsTable),
        "Departures (Checked-in)",
        new JScrollPane(checkoutsTable));

    content.add(activity, BorderLayout.CENTER);
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(BG);
    header.setBorder(new EmptyBorder(0, 0, 12, 0));

    JLabel title = new JLabel("Dashboard");
    title.setFont(new Font("SansSerif", Font.BOLD, 28));
    title.setForeground(TEXT);

    JLabel subtitle = new JLabel("Quick view of occupancy, revenue, and today’s operations");
    subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
    subtitle.setForeground(MUTED);

    JPanel left = new JPanel();
    left.setBackground(BG);
    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
    left.add(title);
    left.add(Box.createVerticalStrut(4));
    left.add(subtitle);

    header.add(left, BorderLayout.WEST);
    return header;
  }

  private JPanel buildOccupancyProgress() {
    JPanel wrap = new JPanel(new BorderLayout());
    wrap.setBackground(CARD_BG);
    wrap.setBorder(new EmptyBorder(4, 0, 0, 0));

    occupancyBar.setValue(0);
    occupancyBar.setStringPainted(false);
    occupancyBar.setBorderPainted(false);
    occupancyBar.setPreferredSize(new Dimension(200, 10));
    occupancyBar.setForeground(Color.decode("#433fca"));
    occupancyBar.setBackground(Color.decode("#eef2ff"));

    wrap.add(occupancyBar, BorderLayout.CENTER);
    return wrap;
  }

  private JPanel buildTwoPanelSection(String title, String leftTitle, JScrollPane left, String rightTitle,
      JScrollPane right) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(CARD_BG);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER),
        new EmptyBorder(16, 16, 16, 16)));

    JLabel header = new JLabel(title);
    header.setFont(TITLE_FONT);
    header.setForeground(TEXT);
    header.setBorder(new EmptyBorder(0, 0, 12, 0));
    card.add(header, BorderLayout.NORTH);

    JPanel grid = new JPanel(new java.awt.GridLayout(1, 2, 16, 0));
    grid.setBackground(CARD_BG);

    grid.add(buildTablePanel(leftTitle, left));
    grid.add(buildTablePanel(rightTitle, right));

    card.add(grid, BorderLayout.CENTER);
    return card;
  }

  private JPanel buildTablePanel(String title, JScrollPane scroll) {
    JPanel p = new JPanel(new BorderLayout(0, 10));
    p.setBackground(CARD_BG);

    JLabel t = new JLabel(title);
    t.setFont(new Font("Poppins", Font.BOLD, 14));
    t.setForeground(TEXT);

    scroll.setBorder(BorderFactory.createLineBorder(BORDER));
    scroll.getViewport().setBackground(Color.WHITE);

    p.add(t, BorderLayout.NORTH);
    p.add(scroll, BorderLayout.CENTER);
    return p;
  }

  private void styleTable(JTable table) {
    table.setRowHeight(28);
    table.setShowVerticalLines(false);
    table.setGridColor(BORDER);
    table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().setPreferredSize(new Dimension(0, 32));
    table.setFillsViewportHeight(true);
  }

  // ===== Controller-facing setters =====

  public void setOccupancy(int occupiedRooms, int totalRooms) {
    int pct = 0;
    if (totalRooms > 0)
      pct = (int) Math.round((occupiedRooms * 100.0) / totalRooms);

    occupancyCard.setValue(pct + "%");
    occupancyCard.setSub(occupiedRooms + " / " + totalRooms + " occupied");
    occupancyBar.setValue(pct);
  }

  public void setRoomsAvailable(int available, int totalRooms) {
    roomsAvailableCard.setValue(String.valueOf(available));
    roomsAvailableCard.setSub("Out of " + totalRooms + " total rooms");
  }

  public void setArrivalsToday(int count) {
    arrivalsTodayCard.setValue(String.valueOf(count));
    arrivalsTodayCard.setSub("Guests to check-in today");
  }

  public void setDeparturesToday(int count) {
    departuresTodayCard.setValue(String.valueOf(count));
    departuresTodayCard.setSub("Guests to check-out today");
  }

  public void setTodayEarnings(double amountPhp) {
    todayRevenueCard.setValue(formatPhp(amountPhp));
  }

  public void setMonthEarnings(double amountPhp) {
    monthRevenueCard.setValue(formatPhp(amountPhp));
  }

  public void setArrivalsRows(Object[][] rows) {
    arrivalsModel.setRowCount(0);
    if (rows == null)
      return;
    for (Object[] r : rows)
      arrivalsModel.addRow(r);
  }

  public void setCheckoutRows(Object[][] rows) {
    checkoutsModel.setRowCount(0);
    if (rows == null)
      return;
    for (Object[] r : rows)
      checkoutsModel.addRow(r);
  }

  private String formatPhp(double amount) {
    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("en", "PH"));
    String s = nf.format(amount);
    return s;
  }

  private static class KpiCard extends JPanel {
    private final JLabel title = new JLabel();
    private final JLabel value = new JLabel();
    private final JLabel sub = new JLabel();

    private final JPanel extras = new JPanel();
    private int extraCount = 0;

    KpiCard(String titleText, String valueText, String subText) {
      setLayout(new BorderLayout());
      setBackground(CARD_BG);
      setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(BORDER),
          new EmptyBorder(14, 14, 14, 14)));

      title.setText(titleText);
      title.setFont(KPI_TITLE_FONT);
      title.setForeground(MUTED);

      value.setText(valueText);
      value.setFont(KPI_VALUE_FONT);
      value.setForeground(TEXT);
      value.setHorizontalAlignment(SwingConstants.LEFT);

      sub.setText(subText);
      sub.setFont(KPI_SUB_FONT);
      sub.setForeground(MUTED);

      JPanel top = new JPanel();
      top.setBackground(CARD_BG);
      top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
      top.add(title);
      top.add(Box.createVerticalStrut(6));
      top.add(value);
      top.add(Box.createVerticalStrut(4));
      top.add(sub);

      extras.setBackground(CARD_BG);
      extras.setLayout(new BoxLayout(extras, BoxLayout.Y_AXIS));

      add(top, BorderLayout.NORTH);
      add(extras, BorderLayout.CENTER);
    }

    void setValue(String v) {
      value.setText(v);
    }

    void setSub(String s) {
      sub.setText(s);
    }

    void addExtra(JPanel component, int topGap) {
      if (extraCount == 0)
        extras.setBorder(new EmptyBorder(topGap, 0, 0, 0));
      extras.add(component);
      extraCount++;
      revalidate();
      repaint();
    }
  }
}
