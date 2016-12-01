package pl.edu.zut.mad.appzut.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.network.HttpConnect;

public class LoginFragment extends Fragment {
    @BindView(R.id.input_login)
    TextInputEditText loginEt;

    @BindView(R.id.input_layout_login)
    TextInputLayout loginTil;

    @BindView(R.id.input_password)
    TextInputEditText passwordEt;

    @BindView(R.id.input_layout_password)
    TextInputLayout passwordTil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private boolean validate() {
        boolean valid = true;
        if (loginEt.getText().toString().isEmpty()) {
            valid = false;
            loginTil.setError(getResources().getString(R.string.field_cannot_be_empty));
        } else {
            loginTil.setError(null);
        }
        if (passwordEt.getText().toString().isEmpty()) {
            valid = false;
            passwordTil.setError(getResources().getString(R.string.field_cannot_be_empty));
        } else {
            passwordTil.setError(null);
        }
        return valid;
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        if (!validate())
            return;

        if(!HttpConnect.isOnline(getContext())) {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment f = new WebPlanFragment();
        Bundle b = new Bundle();
        b.putString(WebPlanFragment.USERNAME_ARG, loginEt.getText().toString());
        b.putString(WebPlanFragment.PASSWORD_ARG, passwordEt.getText().toString());
        f.setArguments(b);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();
    }
}
