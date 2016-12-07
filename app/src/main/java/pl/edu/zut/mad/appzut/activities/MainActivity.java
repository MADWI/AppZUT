package pl.edu.zut.mad.appzut.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.fragments.CalendarFragment;
import pl.edu.zut.mad.appzut.fragments.ScheduleFragment;

public class MainActivity extends AppCompatActivity {

    private static final String CALENDAR_TAG = "calendar_fragment";
    private static final String SCHEDULE_TAG = "schedule_fragment";
    private CalendarFragment calendarFragment;
    private ScheduleFragment scheduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragments(savedInstanceState);
    }

    private void initFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            calendarFragment = new CalendarFragment();
            scheduleFragment = new ScheduleFragment();
            startFragments();
        } else {
            initFragmentsFromStack();
        }
        registerCalendarForScheduleFragment();
    }

    private void startFragments() {
        replaceFragmentInViewContainer(calendarFragment, R.id.calendar_container, CALENDAR_TAG);
        replaceFragmentInViewContainer(scheduleFragment, R.id.schedule_container, SCHEDULE_TAG);
    }

    private void replaceFragmentInViewContainer(Fragment fragment, int containerViewId, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerViewId, fragment, tag)
                .commit();
    }
    
    private void initFragmentsFromStack() {
        calendarFragment = (CalendarFragment) getFragmentFromStackWithTag(CALENDAR_TAG);
        scheduleFragment = (ScheduleFragment) getFragmentFromStackWithTag(SCHEDULE_TAG);
    }

    private Fragment getFragmentFromStackWithTag(String tag) {
        Fragment fragment = null;
        switch (tag) {
            case CALENDAR_TAG:
                fragment = getFragmentWithTag(CALENDAR_TAG);
                if (fragment instanceof CalendarFragment) {
                    return fragment;
                }
            case SCHEDULE_TAG:
                fragment = getFragmentWithTag(SCHEDULE_TAG);
                if (fragment instanceof CalendarFragment) {
                    return fragment;
                }
            default:
                return fragment;
        }
    }

    private Fragment getFragmentWithTag(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }
    
    private void registerCalendarForScheduleFragment() {
        if (scheduleFragment != null) {
            scheduleFragment.registerCalendar(calendarFragment);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleFragment.unregisterCalendar();
    }
}
