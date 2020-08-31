package com.example.weather;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    // Format used for storing dates in the database. ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFriendlyDayString(Context context, Long timeInMs) {
        Log.d("Time","time from DB " + timeInMs);
        Date convertedDate = new Date(timeInMs * 1000L);
        // Date, stored as Text with format yyyy-MM-dd
        String dateStr = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(convertedDate);
        Date todayDate = new Date();
        String todayStr = getDbDateString(Calendar.getInstance().getTime());
        Date inputDate = getDateFromDb(dateStr);
        if (todayStr.equals(dateStr)) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;

            return context.getString(formatId,
                    today,
                    getFormattedMonthDay(context, dateStr));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 7);
            String weekFutureString = getDbDateString(cal.getTime());

            if (dateStr.compareTo(weekFutureString) < 0) {
                return getDayName(context, dateStr);
            } else {
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat(DATE_FORMAT);
                return shortenedDateFormat.format(inputDate);
            }
        }
    }

    public static String getDayName(Context context, String dateStr) {

        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            Date todayDate = new Date();
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            if (getDbDateString(todayDate).equals(dateStr)) {
                return context.getString(R.string.today);
            } else {
                // If the date is set for tomorrow, the format is "Tomorrow".
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, 1);
                Date tomorrowDate = cal.getTime();
                if (getDbDateString(tomorrowDate).equals(
                        dateStr)) {
                    return context.getString(R.string.tomorrow);
                } else {
                    // Otherwise, the format is just the day of the week (e.g "Wednesday".
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                    return dayFormat.format(inputDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // It couldn't process the date correctly.
            return "";
        }
    }

    public static String getFormattedMonthDay(Context context, String dateStr) {
        // TODO Auto-generated method stub
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
            String monthDayString = monthDayFormat.format(inputDate);
            return monthDayString;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatTemperature(Context context, double temperature) {
        return context.getString(R.string.format_temperature, temperature);
    }
}
