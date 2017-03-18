package pl.edu.zut.mad.appzut.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpBarTitle();

        if (savedInstanceState == null) {
            Fragment f = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();
        }
    }

    private void setUpBarTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.edziekanat);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
