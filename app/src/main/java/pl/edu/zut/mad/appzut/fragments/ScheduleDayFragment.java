package pl.edu.zut.mad.appzut.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.activities.WebPlanActivity;
import pl.edu.zut.mad.appzut.adapters.ScheduleDayAdapter;
import pl.edu.zut.mad.appzut.models.Schedule;
import pl.edu.zut.mad.appzut.network.BaseDataLoader;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;
import pl.edu.zut.mad.appzut.utils.DateUtils;

/**
 * Schedule for a particular day
 *
 * Nested in {@link ScheduleFragment}
 *
 * Note: this is child fragment
 *       (so e.g. {@link #onActivityResult(int, int, Intent)} won't work)
 */
public class ScheduleDayFragment extends Fragment implements BaseDataLoader.DataLoadedListener<Schedule> {

    private static final String DATE_KEY = "date";
    private Date date;
    private ScheduleDayAdapter adapter = new ScheduleDayAdapter();
    private Schedule schedule;
    private BaseDataLoader<Schedule, ?> loader;
    private Unbinder unbinder;
    @BindView(R.id.classes_list) RecyclerView classesListView;
    @BindView(R.id.empty_view) TextView noClassesMessage;
    @BindView(R.id.import_from_edziekanat) Button importFromEdziekanatButton;

    public static ScheduleDayFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putLong(DATE_KEY, date.getTime());

        ScheduleDayFragment fragment = new ScheduleDayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ScheduleDayFragment newInstance(int dayOfWeek) {
        GregorianCalendar calendar = new GregorianCalendar();
        DateUtils.stripTime(calendar);

        int todayWeekDay = calendar.get(Calendar.DAY_OF_WEEK);

        // Rewind calendar to last monday
        int daysFromMonday = todayWeekDay - Calendar.MONDAY;
        if (daysFromMonday < 0) {
            daysFromMonday += 7;
        }
        calendar.add(Calendar.DATE, -daysFromMonday);

        // If we're in weekend skip to next week
        if (todayWeekDay == Calendar.SATURDAY || todayWeekDay == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 7);
        }

        // Now move forward to requested day
        calendar.add(Calendar.DATE, dayOfWeek - Calendar.MONDAY);

        return newInstance(calendar.getTime());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle retainableArguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (retainableArguments != null) {
            date = new Date(retainableArguments.getLong(DATE_KEY));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_day_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        initListView();
        initLoader();

        return view;
    }

    private void initListView() {
        classesListView.setAdapter(adapter);
    }

    private void initLoader() {
        loader = DataLoadingManager
                .getInstance(getContext())
                .getLoader(ScheduleEdzLoader.class);
        loader.registerAndLoad(this);
    }

    @Override
    public void onDataLoaded(Schedule schedule) {
        this.schedule = schedule;
        if (schedule == null) {
            importFromEdziekanatButton.setVisibility(View.VISIBLE);
        } else {
            importFromEdziekanatButton.setVisibility(View.GONE);
            putDataInView();
        }
    }

    private void putDataInView() {
        if (schedule == null) {
            return;
        }

        Schedule.Day scheduleDay = schedule.getScheduleForDate(date);
        if (scheduleDay == null) {
            noClassesMessage.setVisibility(View.VISIBLE);
            adapter.setHoursInDay(null);
        } else {
            List<Schedule.Hour> hoursInDay = Arrays.asList(scheduleDay.getTasks());
            adapter.setHoursInDay(hoursInDay);
            noClassesMessage.setVisibility(View.GONE);
        }
    }

    public void setDate(Date date) {
        this.date = date;
        putDataInView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (date != null) {
            outState.putLong(DATE_KEY, date.getTime());
        }
    }

    @Override
    public void onDestroyView() {
        loader.unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.import_from_edziekanat)
    public void onClick() {
        startActivity(new Intent(getContext(), WebPlanActivity.class));
    }
}
