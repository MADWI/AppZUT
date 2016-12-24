package pl.edu.zut.mad.appzut.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.edu.zut.mad.appzut.R;

public class AboutUsFragment extends Fragment {

    @BindView(R.id.app_version)
    TextView appVersion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, view);
        try {
            PackageInfo pInfo = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
            appVersion.setText("v. " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return view;
    }

    @OnClick(R.id.facebook)
    public void onFacebookClick() {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/MADWIZUT/"));
        startActivity(i);
    }

    @OnClick(R.id.website)
    public void onWebsiteClick() {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mad.zut.edu.pl/"));
        startActivity(i);
    }

    @OnClick(R.id.email)
    public void onEmailClick() {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","mad@zut.edu.pl", null));
        startActivity(i);
    }
}
