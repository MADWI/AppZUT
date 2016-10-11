package pl.edu.zut.mad.appzut.models;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import pl.edu.zut.mad.appzut.utils.DateUtils;

public class Schedule {
    private final Day[] mDays;

    public Schedule(Day[] days) {
        mDays = days;
    }

    public Day[] getDays() {
        return mDays;
    }

    /**
     * Time on which task starts and ends
     */
    public static class TimeRange {
        final int fromHour;
        final int fromMinute;
        final int toHour;
        final int toMinute;

        public TimeRange(int fromHour, int fromMinute, int toHour, int toMinute) {
            this.fromHour = fromHour;
            this.fromMinute = fromMinute;
            this.toHour = toHour;
            this.toMinute = toMinute;
        }
    }

    /**
     * Single task within hour and day
     */
    public static class Hour {
        private final String name;
        private final String room;
        private final String teacher;
        private final String type;
        private final TimeRange time;

        public Hour(String name, String type, String room, String teacher, TimeRange time) {
            this.name = name;
            this.type = type;
            this.room = room;
            this.teacher = teacher;
            this.time = time;
        }

        public String getSubjectName() { return name; }

        public String getRoom() {
            return room;
        }

        public String getLecturer() {
            return teacher;
        }

        public String getType() {
            return type;
        }

        public TimeRange getTime() {
            return time;
        }

        public String getSubjectNameWithType() {
            if (type == null) {
                return name;
            } else {
                return name + " (" + type + ")";
            }
        }

        public String getStartTime() {
            int startHour = time.fromHour;
            int startMinute = time.fromMinute;
            return String.format(Locale.getDefault(), "%d:%02d", startHour, startMinute);
        }

        public String getLecturerWithRoom() {
            String lecturerWithRoom = "";
            if (teacher != null) {
                lecturerWithRoom += teacher;
            }
            if (room != null) {
                lecturerWithRoom += " " + room;
            }
            return lecturerWithRoom;
        }
    }

    public static class Day {
        private final GregorianCalendar mDate;
        private final Hour[] mTasks;

        public Day(GregorianCalendar date, Hour[] tasks) {
            mDate = date;
            mTasks = tasks;
        }

        public GregorianCalendar getDate() {
            return mDate;
        }

        public Hour[] getTasks() {
            return mTasks;
        }
    }

    public Day getScheduleForDate(Date date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        DateUtils.stripTime(calendar);

        for (Day day : mDays) {
            if (calendar.getTimeInMillis() == day.mDate.getTimeInMillis()) {
                return day;
            }
        }
        return null;
    }

    public Hour getUpcomingHour() {
        GregorianCalendar today = new GregorianCalendar();

        // Get current minute in day
        int currentMinute = today.get(Calendar.HOUR_OF_DAY) * 60 + today.get(Calendar.MINUTE);

        // Strip time off date
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (Day day : mDays) {
            // Day that passed
            if (day.getDate().before(today)) {
                continue;
            }

            // Today
            if (day.getDate().equals(today)) {
                // Choose first with non-expired date
                for (Hour hour : day.getTasks()) {
                    TimeRange time = hour.getTime();
                    int activityMinute = (
                            time.fromHour * 60 +
                                    time.toHour * 60 +
                                    time.fromMinute +
                                    time.toMinute) / 2;

                    if (activityMinute > currentMinute) {
                        return hour;
                    }
                }

                continue;
            }

            // Next day
            if (day.getTasks().length != 0) {
                return day.getTasks()[0];
            }
        }

        return null;
    }
}
