package org.hotel.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class DateLabelFormatter extends AbstractFormatter {

  private final String pattern = "yyyy-MM-dd";
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);

  @Override
  public Object stringToValue(String text) throws ParseException {
    return dateFormatter.parse(text);
  }

  @Override
  public String valueToString(Object value) {
    if (value != null) {
      Calendar cal = (Calendar) value;
      return dateFormatter.format(cal.getTime());
    }

    return "";
  }
}
