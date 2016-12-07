package pl.edu.zut.mad.appzut.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.roomorama.caldroid.CalendarHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import hirondelle.date4j.DateTime;

public class DateUtils {
    private static final Locale LOCALE = Locale.US;
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final SimpleDateFormat DATE_FORMATTER
            = new SimpleDateFormat(DATE_FORMAT, LOCALE);

    /**
     * Clear time from given Calendar, leaving only date information
     */
    public static void stripTime(GregorianCalendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static void stripTime(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date.setTime(calendar.getTimeInMillis());
    }

    private static int getDayIndexByCalendarDay(int calendarDayNumber) {
        switch (calendarDayNumber) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
            default:
                return -1;
        }
    }

    public static int getDateDayIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayNumber = calendar.get(Calendar.DAY_OF_WEEK);
        return getDayIndexByCalendarDay(dayNumber);
    }

    public static int getDateDayIndex(@NonNull String dateString) {
        Date date = DateUtils.convertStringToDate(dateString);
        return getDateDayIndex(date);
    }

    public static int getDateMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static String convertDateToString(@NonNull Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static String convertDateToShortString(@NonNull Date date) {
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

    /**
     * @return {@link Date} with zeroed time
     */
    public static Date convertDateTimeToDate(@NonNull DateTime dateTime) {
        return CalendarHelper.convertDateTimeToDate(dateTime);
    }

    public static DateTime convertDateToDateTime(@NonNull Date date) {
        return CalendarHelper.convertDateToDateTime(date);
    }

    /**
     * @param date if null, method generates dates for current week,
     *             otherwise for week which belongs to given date
     */
    public static List<Date> getWeekDates(@Nullable Date date) {
        List<Date> weekDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        for (int i = 0; i < 7; i++) {
            Date dayDate = calendar.getTime();
            weekDates.add(dayDate);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        return weekDates;
    }

    public static List<Date> getNextWeekDatesFromDate(@NonNull Date date) {
        Date nextWeekDate = new Date(date.getTime() + getWeekTimeMillis());
        return getWeekDates(nextWeekDate);
    }

    public static List<Date> getPrevWeekDatesFromDate(@NonNull Date date) {
        Date prevWeekDate = new Date(date.getTime() - getWeekTimeMillis());
        return getWeekDates(prevWeekDate);
    }

    private static int getWeekTimeMillis() {
        return 7 * 24 * 60 * 60 * 1000;
    }
}
