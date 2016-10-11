package pl.edu.zut.mad.appzut.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.activities.WebPlanActivity;
import pl.edu.zut.mad.appzut.adapters.SchedulePagerAdapter;
import pl.edu.zut.mad.appzut.models.Schedule;
import pl.edu.zut.mad.appzut.network.BaseDataLoader;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;
import pl.edu.zut.mad.appzut.utils.DateUtils;

public class ScheduleFragment extends Fragment implements BaseDataLoader.DataLoadedListener<Schedule> {

    private ScheduleEdzLoader scheduleLoader;
    private Unbinder unbinder;
    @BindView(R.id.schedule_main) View scheduleWrapper;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.pager) ViewPager pager;
    @BindView(R.id.schedule_unavailable) View scheduleUnavailableWrapper;
    @BindView(R.id.import_from_edziekanat) TextView importFromEdziekanatButton;
    @BindView(R.id.loading_indicator) ProgressBar loadingIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        setBarTitle();
        initTabsWithViewPager();
        selectPageDependOnCurrentDay(savedInstanceState);
        initLoader();

        return view;
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

    private void initTabsWithViewPager() {
        SchedulePagerAdapter pagerAdapter =
                new SchedulePagerAdapter(getChildFragmentManager(), getContext());
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
    }

    private void selectPageDependOnCurrentDay(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            int tabToSelect = DateUtils.getCurrentDayNumber();
            pager.setCurrentItem(tabToSelect);
        }
    }

    private void initLoader() {
        scheduleLoader = DataLoadingManager.getInstance(getActivity())
                .getLoader(ScheduleEdzLoader.class);
        scheduleLoader.registerAndLoad(this);
    }

    @Override
    public void onDataLoaded(Schedule schedule) {
        if (schedule != null) {
            showSchedule();
        } else {
            showScheduleUnavailable();
        }
    }

    private void showSchedule() {
        scheduleWrapper.setVisibility(View.VISIBLE);
        scheduleUnavailableWrapper.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showScheduleUnavailable() {
        scheduleWrapper.setVisibility(View.GONE);
        scheduleUnavailableWrapper.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        scheduleLoader.unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.import_from_edziekanat)
    public void onClick() {
        startActivity(new Intent(getContext(), WebPlanActivity.class));
    }
}
