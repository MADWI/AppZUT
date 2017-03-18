package pl.edu.zut.mad.appzut.utils;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CalendarHelper;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import hirondelle.date4j.DateTime;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {
    @Rule
    public WithAllLocalesRule mLocalesRule = new WithAllLocalesRule();

    @Test
    public void testGetWeekDates() {
        for (int i = 0; i < 7; i++) {
            List<Date> weekDates = DateUtils.getWeekDates(makeDate(2016, Calendar.DECEMBER, 5 + i));

            assertEquals(7, weekDates.size());
            String note = "Locale=" + Locale.getDefault();
            assertEquals(note, makeDate(2016, Calendar.DECEMBER, 5), weekDates.get(0));
            assertEquals(note, makeDate(2016, Calendar.DECEMBER, 6), weekDates.get(1));
            assertEquals(note, makeDate(2016, Calendar.DECEMBER, 7), weekDates.get(2));
            assertEquals(note, makeDate(2016, Calendar.DECEMBER, 8), weekDates.get(3));
            assertEquals(note, makeDate(2016, Calendar.DECEMBER, 9), weekDates.get(4));
            assertEquals(note, makeDate(2016, Calendar.DECEMBER, 10), weekDates.get(5));
            assertEquals(note, makeDate(2016, Calendar.DECEMBER, 11), weekDates.get(6));
        }
    }

    private Date makeDate(int year, int month, int day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        return cal.getTime();
    }

    /**
     * Check if weeks provided by Caldroid match ours, dates in this test
     * should match those in {@link #testGetWeekDates()}
     */
    @Test
    public void testCaldroidWeeks() {
        ArrayList<DateTime> fullWeeks = CalendarHelper.getFullWeeks(12, 2016, CaldroidFragment.MONDAY, false);
        assertEquals(0, fullWeeks.size() % 7);
        assertEquals(5, (int) fullWeeks.get(7).getDay());
        assertEquals(6, (int) fullWeeks.get(8).getDay());
        assertEquals(7, (int) fullWeeks.get(9).getDay());
        assertEquals(8, (int) fullWeeks.get(10).getDay());
        assertEquals(9, (int) fullWeeks.get(11).getDay());
        assertEquals(10, (int) fullWeeks.get(12).getDay());
        assertEquals(11, (int) fullWeeks.get(13).getDay());
    }
}
