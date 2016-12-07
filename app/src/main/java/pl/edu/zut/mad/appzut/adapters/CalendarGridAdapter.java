package pl.edu.zut.mad.appzut.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;
import pl.edu.zut.mad.appzut.R;

public class CalendarGridAdapter extends CaldroidGridAdapter {

    private static final String BACKGROUND_FOR_DATETIME_MAP = CaldroidFragment._BACKGROUND_FOR_DATETIME_MAP;

    public CalendarGridAdapter(Context context, int month, int year,
                               Map<String, Object> caldroidData,
                               Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    protected void setCustomResources(DateTime dateTime, View backgroundView, TextView textView) {
        int backgroundResource = getBackgroundResourceForDateTime(dateTime);
        if (isToday(dateTime)) {
            setBackgroundResourceWithBorderForView(backgroundResource, backgroundView);
        } else {
            backgroundView.setBackgroundResource(backgroundResource);
        }
    }

    private int getBackgroundResourceForDateTime(DateTime dateTime) {
        if (isSelectedDateTime(dateTime)) {
            return R.color.calendar_cell_selected;
        } else if (isClassesDateTime(dateTime)) {
            return R.color.calendar_cell_classes_day;
        } else {
            return R.color.calendar_cell_default;
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

    private void setBackgroundResourceWithBorderForView(int backgroundResource, View view) {
        Context context = view.getContext();
        Drawable backgroundDrawable = getDrawableFromResource(context, backgroundResource);

        if (backgroundDrawable instanceof ColorDrawable) {
            Drawable todayBorder =
                    ContextCompat.getDrawable(context, R.drawable.calendar_cell_today_border).mutate();
            GradientDrawable borderDrawable = (GradientDrawable) todayBorder;
            int viewBackgroundColor = ((ColorDrawable) backgroundDrawable).getColor();
            borderDrawable.setColor(viewBackgroundColor);
            view.setBackground(borderDrawable);
        }
    }

    private Drawable getDrawableFromResource(@NonNull Context context, int drawableId) {
        return ContextCompat.getDrawable(context, drawableId);
    }
}
