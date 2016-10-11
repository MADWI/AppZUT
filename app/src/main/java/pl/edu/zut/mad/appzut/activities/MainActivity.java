package pl.edu.zut.mad.appzut.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.fragments.CalendarFragment;
import pl.edu.zut.mad.appzut.fragments.ScheduleFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            startFragment(ScheduleFragment.class);
        }
    }

    private void startFragment(Class<? extends Fragment> fragmentClass) {
        Fragment fragment = createFragmentInstance(fragmentClass);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    private Fragment createFragmentInstance(Class<? extends Fragment> fragmentClass) {
        try {
            return fragmentClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.calendar) {
            startFragment(CalendarFragment.class);
        } else if (itemId == R.id.week_schedule) {
            startFragment(ScheduleFragment.class);
        }
        return false;
    }
}
