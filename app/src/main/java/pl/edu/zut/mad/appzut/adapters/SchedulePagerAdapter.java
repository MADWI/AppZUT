package pl.edu.zut.mad.appzut.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.fragments.ScheduleDayFragment;
import pl.edu.zut.mad.appzut.utils.DateUtils;

public class SchedulePagerAdapter extends FragmentPagerAdapter {

    private final String[] dayNames;

    public SchedulePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        dayNames = context.getResources().getStringArray(R.array.week_days_short);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dayNames[position];
    }

    @Override
    public Fragment getItem(int position) {
        int day = DateUtils.getDayByNumber(position);
        return ScheduleDayFragment.newInstance(day);
    }
}
