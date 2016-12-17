package pl.edu.zut.mad.appzut.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CellView;

import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;
import pl.edu.zut.mad.appzut.R;

public class CalendarGridAdapter extends CaldroidGridAdapter {

    private static final String BACKGROUND_FOR_DATETIME_MAP = CaldroidFragment._BACKGROUND_FOR_DATETIME_MAP;
    private final Drawable mSelectedCellBackground;
    private final Drawable mClassesCellBackground;
    private final Drawable mDefaultCellBackground;

    public CalendarGridAdapter(Context context, int month, int year,
                               Map<String, Object> caldroidData,
                               Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
        mSelectedCellBackground = ContextCompat.getDrawable(context, R.color.calendar_cell_selected);
        mClassesCellBackground = ContextCompat.getDrawable(context, R.color.calendar_cell_classes_day);
        mDefaultCellBackground = ContextCompat.getDrawable(context, R.color.calendar_cell_default);
    }

    @Override
    protected void setCustomResources(DateTime dateTime, View backgroundView, TextView textView) {
        Drawable backgroundResource = getBackgroundForDateTime(dateTime);
        if (isToday(dateTime)) {
            setBackgroundWithBorderForView(backgroundResource, backgroundView);
        } else {
            backgroundView.setBackground(backgroundResource);
        }
    }

    private Drawable getBackgroundForDateTime(DateTime dateTime) {
        if (isSelectedDateTime(dateTime)) {
            return mSelectedCellBackground;
        } else if (isClassesDateTime(dateTime)) {
            return mClassesCellBackground;
        } else {
            return mDefaultCellBackground;
        }
    }

    private boolean isSelectedDateTime(DateTime dateTime) {
        return selectedDatesMap.containsKey(dateTime);
    }

    private boolean isClassesDateTime(DateTime dateTime) {
        HashMap<DateTime, Drawable> backgroundForDateTimeMap =
                (HashMap<DateTime, Drawable>) caldroidData.get(BACKGROUND_FOR_DATETIME_MAP);
        return hasMapDateTime(backgroundForDateTimeMap, dateTime);
    }

    private boolean hasMapDateTime(Map map, DateTime dateTime) {
        return map != null && map.containsKey(dateTime);
    }

    private boolean isToday(DateTime dateTime) {
        return dateTime.equals(getToday());
    }

    private void setBackgroundWithBorderForView(Drawable backgroundDrawable, View view) {
        Context context = view.getContext();

        if (backgroundDrawable instanceof ColorDrawable) {
            Drawable todayBorder =
                    ContextCompat.getDrawable(context, R.drawable.calendar_cell_today_border).mutate();
            GradientDrawable borderDrawable = (GradientDrawable) todayBorder;
            int viewBackgroundColor = ((ColorDrawable) backgroundDrawable).getColor();
            borderDrawable.setColor(viewBackgroundColor);
            view.setBackground(borderDrawable);
        }
    }

    @Override
    protected void customizeTextView(int position, CellView cellView) {
        // This appears in superclass but we don't use setBackgroundResource anymore
        // (see bottom of this method)
        // Get the padding of cell so that it can be restored later
        //int topPadding = cellView.getPaddingTop();
        //int leftPadding = cellView.getPaddingLeft();
        //int bottomPadding = cellView.getPaddingBottom();
        //int rightPadding = cellView.getPaddingRight();

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);

        cellView.resetCustomStates();

        // In superclass this is in resetCustomResources(cellView);
        //cellView.setBackgroundResource(defaultCellBackgroundRes);
        cellView.setTextColor(defaultTextColorRes);
        // End of resetCustomResources(cellView);

        if (dateTime.equals(getToday())) {
            cellView.addCustomState(CellView.STATE_TODAY);
        }

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            cellView.addCustomState(CellView.STATE_PREV_NEXT_MONTH);
        }

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDatesMap
                .containsKey(dateTime))) {

            cellView.addCustomState(CellView.STATE_DISABLED);
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDatesMap.containsKey(dateTime)) {
            cellView.addCustomState(CellView.STATE_SELECTED);
        }

        cellView.refreshDrawableState();

        // Set text
        cellView.setText(String.valueOf(dateTime.getDay()));

        // Set custom color if required
        setCustomResources(dateTime, cellView, cellView);

        // This appears in superclass but we don't use setBackgroundResource anymore
        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        //cellView.setPadding(leftPadding, topPadding, rightPadding,
        //        bottomPadding);
    }
}
