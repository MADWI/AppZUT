package pl.edu.zut.mad.appzut.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.DateGridFragment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;
import pl.edu.zut.mad.appzut.adapters.CalendarGridAdapter;
import pl.edu.zut.mad.appzut.utils.DateUtils;

public class CalendarFragment extends CaldroidFragment {

    private CalendarListener calendarListener;

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalendarGridAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);
    }

    @Override
    protected void retrieveInitialArgs() {
        super.retrieveInitialArgs();
        startDayOfWeek = CaldroidFragment.MONDAY;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setCaldroidListener(listener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // michalbednarski: Hacky workaround for Caldroid's saved state mishandling
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);
    }

    private final CaldroidListener listener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            if (calendarListener != null) {
                calendarListener.onSelectDate(date);
            } else {
                setSelectedDate(date);
            }
        }
    };

    /**
     * This method will be also called from {@link ScheduleFragment} by
     * {@link android.support.v4.view.ViewPager.OnPageChangeListener}
     */
    @Override
    public void setSelectedDate(Date date) {
        DateUtils.stripTime(date);
        clearSelectedDates();
        super.setSelectedDate(date);
        moveToProperMothPageIfNeeded(date);
        refreshView();
    }

    /**
     * When user swipe on calendar and will be moved to next or previous month
     * then next swipe on schedule pager should also set proper calendar month page
     */
    private void moveToProperMothPageIfNeeded(Date date) {
        DateTime dateTime = DateUtils.convertDateToDateTime(date);
        if (!dateInMonthsList.contains(dateTime)) {
            moveToDate(date);
        }
    }

    public void setClassesDates(List<Date> classesDates) {
        putClassesDatesToMap(classesDates);
        updateAdapters();
        refreshView();
    }

    private void putClassesDatesToMap(List<Date> classesDates) {
        Map<Date, Drawable> dateMap = convertClassesListToDateMap(classesDates);
        setBackgroundDrawableForDates(dateMap);
    }

    /**
     * Put null as drawable because we use
     * {@link CalendarGridAdapter#getBackgroundForDateTime(DateTime)} for getting
     * proper background drawable (e.g. it could have a border for current day or be selected)
     */
    private Map<Date, Drawable> convertClassesListToDateMap(List<Date> classesDates) {
        Map<Date, Drawable> dateMap = new HashMap<>();
        for (Date date : classesDates) {
            dateMap.put(date, null);
        }
        return dateMap;
    }

    private void updateAdapters() {
        for (CaldroidGridAdapter adapter : datePagerAdapters) {
            adapter.setCaldroidData(getCaldroidData());
        }
    }

    public void setCalendarListener(CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    public boolean isLastDateInMonthPage(@NonNull Date date) {
        int lastIndex = dateInMonthsList.size();
        DateTime lastDateTime = dateInMonthsList.get(lastIndex - 1);
        Date lastDate = DateUtils.convertDateTimeToDate(lastDateTime);
        return date.compareTo(lastDate) == 0;
    }

    public boolean isFirstDateInMonthPage(Date date) {
        DateTime lastDateTime = dateInMonthsList.get(0);
        Date firstDate = DateUtils.convertDateTimeToDate(lastDateTime);
        return date.compareTo(firstDate) == 0;
    }

    /**
     * Useful method for preventing from click events on calendar date cells
     * (e.g. when is collapsed and covered by toolbar)
     *
     * @param isEnabled set true for enabling click events, otherwise false
     */
    public void setEnabledForClickEvents(boolean isEnabled) {
        List<DateGridFragment> calendarMonthFragments = getCalendarMonthFragments();
        for (DateGridFragment fragment : calendarMonthFragments) {
            ViewGroup viewGroup = fragment.getGridView();
            setEnableClickForViewGroup(viewGroup, isEnabled);
        }
    }

    private void setEnableClickForViewGroup(ViewGroup viewGroup, boolean isEnabled) {
        if (viewGroup == null) {
            return;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(isEnabled);
        }
    }

    private List<DateGridFragment> getCalendarMonthFragments() {
        return getFragments();
    }

    interface CalendarListener {
        void onSelectDate(Date date);
    }
}
