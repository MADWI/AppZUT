package pl.edu.zut.mad.appzut.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.fragments.AboutUsFragment;
import pl.edu.zut.mad.appzut.fragments.CalendarFragment;
import pl.edu.zut.mad.appzut.fragments.ScheduleFragment;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.HttpConnect;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;
import pl.edu.zut.mad.appzut.utils.User;

public class MainActivity extends AppCompatActivity {

    private static final String CALENDAR_TAG = "calendar_fragment";
    private static final String SCHEDULE_TAG = "schedule_fragment";
    private CalendarFragment calendarFragment;
    private ScheduleFragment scheduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBar();

        User user = User.getInstance(this);
        if (user.isSaved()) {
            initScheduleFragments(savedInstanceState);
        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void initBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_logo_zut);
            getSupportActionBar().setTitle("ZUT");
        }
    }

    private void initScheduleFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            calendarFragment = new CalendarFragment();
            scheduleFragment = new ScheduleFragment();
            startScheduleFragments();
        } else {
            initScheduleFragmentsFromStack();
        }
        registerCalendarForScheduleFragment();
    }

    private void startScheduleFragments() {
        replaceFragmentInViewContainer(calendarFragment, R.id.calendar_container, CALENDAR_TAG);
        replaceFragmentInViewContainer(scheduleFragment, R.id.schedule_container, SCHEDULE_TAG);
    }

    private void replaceFragmentInViewContainer(Fragment fragment, int containerViewId, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerViewId, fragment, tag)
                .commit();
    }

    private void initScheduleFragmentsFromStack() {
        calendarFragment = (CalendarFragment) getFragmentFromStackWithTag(CALENDAR_TAG);
        scheduleFragment = (ScheduleFragment) getFragmentFromStackWithTag(SCHEDULE_TAG);
    }

    private Fragment getFragmentFromStackWithTag(String tag) {
        Fragment fragment = null;
        switch (tag) {
            case CALENDAR_TAG:
                fragment = getFragmentWithTag(CALENDAR_TAG);
                if (fragment instanceof CalendarFragment) {
                    return fragment;
                }
            case SCHEDULE_TAG:
                fragment = getFragmentWithTag(SCHEDULE_TAG);
                if (fragment instanceof CalendarFragment) {
                    return fragment;
                }
            default:
                return fragment;
        }
    }

    private Fragment getFragmentWithTag(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    private void registerCalendarForScheduleFragment() {
        if (scheduleFragment != null) {
            scheduleFragment.registerCalendar(calendarFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.action_authors:
                aboutUs();
                return true;
            case R.id.action_refresh:
                refreshScheduleIfNetworkAvailable();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        ScheduleEdzLoader loader = DataLoadingManager
                .getInstance(this)
                .getLoader(ScheduleEdzLoader.class);
        loader.setSourceTableJson("");

        SharedPreferences settings = getSharedPreferences(User.PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        settings.edit().clear().apply();

        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void aboutUs() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_container, new AboutUsFragment(), "AboutUs")
                .addToBackStack(null)
                .commit();
    }

    private void refreshScheduleIfNetworkAvailable() {
        if (HttpConnect.isOnline(this)) {
            refreshSchedule();
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshSchedule() {
        User user = User.getInstance(this);
        String login = user.getSavedLogin();
        String password = user.getSavedPassword();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(User.LOGIN_KEY, login);
        intent.putExtra(User.PASSWORD_KEY, password);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduleFragment != null) {
            scheduleFragment.unregisterCalendar();
        }
    }
}
