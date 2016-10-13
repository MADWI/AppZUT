package pl.edu.zut.mad.appzut.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.models.Schedule;
import pl.edu.zut.mad.appzut.network.BaseDataLoader;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;
import pl.edu.zut.mad.appzut.utils.DateUtils;

public class CalendarFragment extends CaldroidFragment implements BaseDataLoader.DataLoadedListener<Schedule> {

    private static final String SELECTED_DATE = "date";
    private Schedule schedule;
    private ScheduleEdzLoader scheduleLoader;
    private Drawable selectedDateBackgroundColor;
    private Drawable classesDayBackgroundColor;
    private Date selectedDate = new Date();
    private ScheduleDayFragment scheduleDayFragment = new ScheduleDayFragment();
    @BindView(R.id.calendar_container) FrameLayout calendarContainer;
    @BindView(R.id.schedule_container) FrameLayout scheduleContainer;

    @Override
    protected void retrieveInitialArgs() {
        startDayOfWeek = CaldroidFragment.MONDAY;
        super.retrieveInitialArgs();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setCaldroidListener(listener);
        classesDayBackgroundColor = ContextCompat.getDrawable(context, R.color.colorAccent);
        selectedDateBackgroundColor = ContextCompat.getDrawable(context, R.color.colorPrimaryDark);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // michalbednarski: Hacky workaround for Caldroid's saved state mishandling
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
            String selectedDateString = savedInstanceState.getString(SELECTED_DATE, "");
            this.selectedDate = DateUtils.convertStringToDate(selectedDateString);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        View calendarView = super.onCreateView(inflater, container, savedInstanceState);
        calendarContainer.addView(calendarView);

        setBarTitle();
        loadSchedule();
        startScheduleFragment();

        return rootView;
    }

    private void setBarTitle() {
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ActionBar bar = ((AppCompatActivity) activity).getSupportActionBar();
            if (bar != null) {
                bar.setTitle(R.string.schedule);
            }
        }
    }

    private void loadSchedule() {
        scheduleLoader = DataLoadingManager.getInstance(getActivity()).getLoader(ScheduleEdzLoader.class);
        scheduleLoader.registerAndLoad(this);
    }

    @Override
    public void onDataLoaded(Schedule schedule) {
        if (schedule == null) {
            return;
        }
        this.schedule = schedule;
        colorDaysWithClasses();
        listener.onSelectDate(selectedDate, null);
    }

    private void colorDaysWithClasses() {
        Schedule.Day[] days = schedule.getDays();
        for (Schedule.Day day : days) {
            setBackgroundDrawableForDate(classesDayBackgroundColor, day.getDate().getTime());
        }
    }

    private void startScheduleFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.schedule_container, scheduleDayFragment)
                .commit();
    }

    private final CaldroidListener listener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            clearBackgroundDrawableForDate(selectedDate);
            colorDaysWithClasses();
            setBackgroundDrawableForDate(selectedDateBackgroundColor, date);
            scheduleDayFragment.setDate(date);
            refreshView();
            selectedDate = date;
        }
    };

    @Override
    public void clearBackgroundDrawableForDate(@Nullable Date date) {
        if (date != null) {
            super.clearBackgroundDrawableForDate(date);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String date = DateUtils.convertDateToString(selectedDate);
        outState.putString(SELECTED_DATE, date);
    }

    @Override
    public void onDestroyView() {
        scheduleLoader.unregister(this);
        super.onDestroyView();
    }
}
