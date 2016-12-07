package pl.edu.zut.mad.appzut.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.tobishiba.circularviewpager.library.BaseCircularViewPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.edu.zut.mad.appzut.fragments.ScheduleDayFragment;

public class SchedulePagerAdapter extends BaseCircularViewPagerAdapter<Date> {

    public SchedulePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, new ArrayList<Date>());
    }

    @Override
    protected Fragment getFragmentForItem(Date date) {
        return ScheduleDayFragment.newInstance(date);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setWeekDates(List<Date> weekDates ) {
        setItems(weekDates);
    }
}
