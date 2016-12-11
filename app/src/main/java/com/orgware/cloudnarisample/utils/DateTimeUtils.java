package com.orgware.cloudnarisample.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nandagopal on 12/8/16.
 */
public class DateTimeUtils {

  public static String splitFromString(String stringName) {

    try {
      SimpleDateFormat mRequiredDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
      SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
      Date date = mSimpleDateFormat.parse(stringName);

      return mRequiredDateFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return "";
  }
}
