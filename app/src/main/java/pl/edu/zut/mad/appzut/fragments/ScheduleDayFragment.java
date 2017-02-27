package pl.edu.zut.mad.appzut.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.adapters.ScheduleDayAdapter;
import pl.edu.zut.mad.appzut.models.Schedule;
import pl.edu.zut.mad.appzut.network.BaseDataLoader;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;

public class ScheduleDayFragment extends Fragment implements BaseDataLoader.DataLoadedListener<Schedule> {

    private static final String DATE_KEY = "date";
    private ScheduleDayAdapter adapter;
    private Date date;
    private Schedule schedule;
    private BaseDataLoader<Schedule, ?> loader;
    private Unbinder unbinder;
    @BindView(R.id.classes_list) RecyclerView classesListView;
    @BindView(R.id.empty_view) View noClassesMessage;

    public static ScheduleDayFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putLong(DATE_KEY, date.getTime());

        ScheduleDayFragment fragment = new ScheduleDayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle retainableArguments = getArguments();
        if (retainableArguments != null) {
            date = new Date(retainableArguments.getLong(DATE_KEY));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_day_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new ScheduleDayAdapter(getContext());
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
        if (schedule != null) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (date != null) {
            outState.putLong(DATE_KEY, date.getTime());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loader.unregister(this);
        unbinder.unbind();
    }
}
