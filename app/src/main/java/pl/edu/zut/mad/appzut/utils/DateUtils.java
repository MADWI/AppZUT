package pl.edu.zut.mad.appzut.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMATTER
            = new SimpleDateFormat(DATE_FORMAT, java.util.Locale.US);

    /**
     * Get date of easter for specified day
     *
     * @return "month day" (two numbers separated by space)
     *
     * usage of algorithm to find Easter Sunday date found by Carl Friedrich Gauss
     * usage example from http://stackoverflow.com/questions/26022233/calculate-the-date-of-easter-sunday
     */
    public static String getEasterSundayDate(int year) {
        int a = year % 19,
                b = year / 100,
                c = year % 100,
                d = b / 4,
                e = b % 4,
                g = (8 * b + 13) / 25,
                h = (19 * a + b - d - g + 15) % 30,
                j = c / 4,
                k = c % 4,
                m = (a + 11 * h) / 319,
                r = (2 * e + 2 * j - k - h + m + 32) % 7,
                n = (h - m + r + 90) / 25,
                p = (h - m + r + n + 19) % 32;

        //n - month, p - day
        return String.valueOf(n) + " " + String.valueOf(p);
    }

    /**
     * Clear time from given Calendar, leaving only date information
     */
    public static void stripTime(GregorianCalendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static int getCurrentDayNumber() {
        int weekday = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
        return weekday == Calendar.MONDAY    ? 0 :
               weekday == Calendar.TUESDAY   ? 1 :
               weekday == Calendar.WEDNESDAY ? 2 :
               weekday == Calendar.THURSDAY  ? 3 :
               weekday == Calendar.FRIDAY    ? 4 :
                -1;
    }

    public static int getDayByNumber(int number) {
        return number == 0 ? Calendar.MONDAY :
               number == 1 ? Calendar.TUESDAY :
               number == 2 ? Calendar.WEDNESDAY :
               number == 3 ? Calendar.THURSDAY :
               number == 4 ? Calendar.FRIDAY :
                -1;
    }

    public static String convertDateToString(@NonNull Date date) {
        return DATE_FORMATTER.format(date);
    }

    @Nullable
    public static Date convertStringToDate(@NonNull String date) {
        try {
            return DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
