package pl.edu.zut.mad.appzut.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tobishiba.circularviewpager.library.CircularViewPagerHandler;

import java.util.Date;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.activities.LoginActivity;
import pl.edu.zut.mad.appzut.adapters.SchedulePagerAdapter;
import pl.edu.zut.mad.appzut.models.Schedule;
import pl.edu.zut.mad.appzut.network.BaseDataLoader;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;
import pl.edu.zut.mad.appzut.utils.DateUtils;

public class ScheduleFragment extends Fragment
        implements BaseDataLoader.DataLoadedListener<Schedule>, CalendarFragment.CalendarListener,
                   AppBarLayout.OnOffsetChangedListener {

    private static final String SELECTED_DATE = "selected_date";
    private static final String EMPTY_TOOLBAR_TITLE = " ";
    private static final float COLLAPSED_PERCENT_SHOW_TITLE = 0.65f;
    private static final int FIRST_DAY_INDEX = 0;
    private static final int LAST_DAY_INDEX = 6;
    @BindView(R.id.schedule_main) View scheduleWrapper;
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.schedule_unavailable) View scheduleUnavailableWrapper;
    @BindView(R.id.loading_indicator) ProgressBar loadingIndicator;
    @BindArray(R.array.week_days) String[] weekDaysNames;
    private Unbinder unbinder;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private CalendarFragment calendarFragment;
    private ScheduleEdzLoader scheduleLoader;
    private SchedulePagerAdapter pagerAdapter;
    private List<Date> currentWeekDates;
    private String selectedDateString;
    private boolean isBarTitleShow = false;
    private int barShowHeight = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        readSavedSelectedDateString(savedInstanceState);
        initPager();
        initLoader();
        updateCurrentWeekDatesForSelectedDate();
        return rootView;
    }

    private void readSavedSelectedDateString(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            selectedDateString = savedInstanceState.getString(SELECTED_DATE);
        } else {
            selectedDateString = DateUtils.convertDateToString(new Date());
        }
    }

    private void initPager() {
        pagerAdapter = new SchedulePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        CircularViewPagerHandler circularViewPagerHandler = new CircularViewPagerHandler(viewPager);
        circularViewPagerHandler.setOnPageChangeListener(onPageChangeListener);
        viewPager.addOnPageChangeListener(circularViewPagerHandler);
    }

    private final ViewPager.SimpleOnPageChangeListener onPageChangeListener
            = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (position < FIRST_DAY_INDEX) {
                Date date = currentWeekDates.get(FIRST_DAY_INDEX);
                updateCurrentWeekDatesForPrevWeek(date);
                moveToPrevMonthPageIfNeeded(date);
            } else if (position > LAST_DAY_INDEX) {
                Date date = currentWeekDates.get(LAST_DAY_INDEX);
                updateCurrentWeekDatesForNextWeek(date);
                moveToNextMonthPageIfNeeded(date);
            } else {
                Date currentDate = currentWeekDates.get(position);
                calendarFragment.setSelectedDate(currentDate);
                selectedDateString = DateUtils.convertDateToShortString(currentDate);
            }
        }
    };

    private void updateCurrentWeekDatesForPrevWeek(@NonNull Date date) {
        currentWeekDates = DateUtils.getPrevWeekDatesFromDate(date);
        pagerAdapter.setWeekDates(currentWeekDates);
    }

    private void moveToPrevMonthPageIfNeeded(@NonNull Date date) {
        if (calendarFragment.isFirstDateInMonthPage(date)) {
            calendarFragment.prevMonth();
        }
    }

    private void updateCurrentWeekDatesForNextWeek(@NonNull Date date) {
        currentWeekDates = DateUtils.getNextWeekDatesFromDate(date);
        pagerAdapter.setWeekDates(currentWeekDates);
    }

    private void moveToNextMonthPageIfNeeded(@NonNull Date date) {
        if (calendarFragment.isLastDateInMonthPage(date)) {
            calendarFragment.nextMonth();
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
            calendarFragment.setClassesDates(schedule.getClassesDates());
            selectCurrentDayPage();
            showSchedule();
        } else {
            showScheduleUnavailable();
        }
    }

    private void selectCurrentDayPage() {
        int dayIndex = DateUtils.getDateDayIndex(selectedDateString);
        viewPager.setCurrentItem(dayIndex + 1);
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

    private void updateCurrentWeekDatesForSelectedDate() {
        Date date = DateUtils.convertStringToDate(selectedDateString);
        updateCurrentWeekDates(date);
    }

    private void updateCurrentWeekDates(@Nullable Date dayDate) {
        currentWeekDates = DateUtils.getWeekDates(dayDate);
        pagerAdapter.setWeekDates(currentWeekDates);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAppBarLayout();
        toolbarLayout = ButterKnife.findById(getActivity(), R.id.collapsing_toolbar);
        toolbar = ButterKnife.findById(getActivity(), R.id.toolbar);
    }

    private void initAppBarLayout() {
        AppBarLayout appBarLayout = ButterKnife.findById(getActivity(), R.id.app_bar_layout);
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(this);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int totalScrollRange = appBarLayout.getTotalScrollRange();
        initBarShowHeightIfNeeded(totalScrollRange);
        enableOrDisableClickEventsOnCalendar(verticalOffset, totalScrollRange);
        showOrHideToolbarTitle(verticalOffset);
    }

    private void initBarShowHeightIfNeeded(int totalScrollRange) {
        if (barShowHeight == -1) {
            barShowHeight = totalScrollRange;
            barShowHeight *= COLLAPSED_PERCENT_SHOW_TITLE;
        }
    }

    private void enableOrDisableClickEventsOnCalendar(int verticalOffset, int totalScrollRange) {
        if (verticalOffset == 0) {
            toolbar.setVisibility(View.GONE);
            calendarFragment.setEnabledForClickEvents(true);
        } else if (Math.abs(verticalOffset) >= totalScrollRange) {
            calendarFragment.setEnabledForClickEvents(false);
        }
    }

    private void showOrHideToolbarTitle(int verticalOffset) {
        if (shouldShowTitle(verticalOffset)) {
            String dayName = getSelectedDayName();
            String toolbarTitle = dayName + " " + selectedDateString;
            toolbarLayout.setTitle(toolbarTitle);
            isBarTitleShow = true;
        } else if (isBarTitleShow) {
            toolbarLayout.setTitle(EMPTY_TOOLBAR_TITLE);
            isBarTitleShow = false;
        }
    }

    private boolean shouldShowTitle(int verticalOffset) {
        return barShowHeight + verticalOffset <= 0;
    }

    private String getSelectedDayName() {
        int dayIndex = DateUtils.getDateDayIndex(selectedDateString);
        return weekDaysNames[dayIndex];
    }

    @Override
    public void onSelectDate(Date date) {
        selectedDateString = DateUtils.convertDateToShortString(date);
        updateCurrentWeekDatesIfNeeded(date);
        int dayIndex = DateUtils.getDateDayIndex(date);
        callViewPagerListener(dayIndex);
    }

    /**
     * When current item in {@link ViewPager} is the same as last selected date index,
     * {@link ViewPager#setCurrentItem(int)} will not call
     * {@link android.support.v4.view.ViewPager.OnPageChangeListener} so we need to do it manually
     */
    private void callViewPagerListener(int dayIndex) {
        int pageToSelect = dayIndex + 1;
        if (viewPager.getCurrentItem() == pageToSelect) {
            onPageChangeListener.onPageSelected(dayIndex);
        }
        viewPager.setCurrentItem(pageToSelect, true);
    }

    private void updateCurrentWeekDatesIfNeeded(@NonNull Date dayDate) {
        if (!isDayDateInCurrentWeek(dayDate)) {
            updateCurrentWeekDates(dayDate);
        }
    }

    private boolean isDayDateInCurrentWeek(@NonNull Date dayDate) {
        for (Date date : currentWeekDates) {
            if (date.compareTo(dayDate) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scheduleLoader.unregister(this);
        unbinder.unbind();
    }

    @OnClick(R.id.import_from_edziekanat)
    public void onClick() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_DATE, selectedDateString);
    }

    public void registerCalendar(Fragment calendarFragment) {
        if (calendarFragment instanceof CalendarFragment) {
            this.calendarFragment = (CalendarFragment) calendarFragment;
            this.calendarFragment.setCalendarListener(this);
        }
    }

    public void unregisterCalendar() {
        this.calendarFragment = null;
    }
}
