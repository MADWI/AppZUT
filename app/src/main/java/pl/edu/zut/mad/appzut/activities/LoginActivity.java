package pl.edu.zut.mad.appzut.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.edziekanat);

        if (savedInstanceState == null) {
            Fragment f = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();
        }
    }
}
