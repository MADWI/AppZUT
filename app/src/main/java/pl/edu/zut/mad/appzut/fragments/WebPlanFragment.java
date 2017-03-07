package pl.edu.zut.mad.appzut.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.activities.MainActivity;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;
import pl.edu.zut.mad.appzut.utils.User;

public class WebPlanFragment extends Fragment {

    @BindView(R.id.web_view_plan)
    WebView web;

    @BindView(R.id.please_wait)
    View pleaseWaitView;

    private String login;
    private String password;

    public static WebPlanFragment newInstance(String login, String password) {
        WebPlanFragment f = new WebPlanFragment();
        Bundle b = new Bundle();
        b.putString(User.LOGIN_KEY, login);
        b.putString(User.PASSWORD_KEY, password);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_plan, container, false);
        ButterKnife.bind(this, view);
        login = getArguments().getString(User.LOGIN_KEY);
        password = getArguments().getString(User.PASSWORD_KEY);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWebView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView() {
        pleaseWaitView.setVisibility(View.VISIBLE);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(this, "android");
        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                web.loadUrl("javascript:(function(){document.body.appendChild(document.createElement('script')).src='/appwizut-injected-script.js'})()");
            }

            @Override
            @SuppressWarnings("deprecation")
            // Newer version is not supported on older Android versions
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.endsWith("/appwizut-injected-script.js")) {
                    return new WebResourceResponse(
                            "text/javascript",
                            "utf-8",
                            getResources().openRawResource(R.raw.grab_schedule)
                    );
                }
                return null;
            }
        });

        web.loadUrl("https://edziekanat.zut.edu.pl/WU");
    }

    @JavascriptInterface
    public String getLogin() {
        return login;
    }

    @JavascriptInterface
    public String getPassword() {
        return password;
    }

    @JavascriptInterface
    public void onTableGrabbed(final String contents) {
        getActivity().runOnUiThread(() -> {
            saveUser(login, password);
            DataLoadingManager
                    .getInstance(getContext())
                    .getLoader(ScheduleEdzLoader.class)
                    .setSourceTableJson(contents);
            getActivity().finish();
            startActivity(new Intent(getContext(), MainActivity.class));
        });
    }

    private void saveUser(String login, String password) {
        User user = User.getInstance(getContext());
        user.save(login, password);
    }

    @JavascriptInterface
    public void onLoginError(String error) {
        if (error != null && !error.equals(getResources().getString(R.string.incorrect_error))) {
            web.post(() -> {
                web.stopLoading();
                web.destroy();
            });
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            goToLoginFragment();
        }
    }

    private void goToLoginFragment() {
        Fragment f = new LoginFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();
    }

    @JavascriptInterface
    public void chooseFieldOfStudy(final String[] fields, final String[] ids) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.choose_field_of_study)
                    .setCancelable(false)
                    .setItems(fields, (dialogInterface, index) -> {
                        dialogInterface.dismiss();
                        passSelectedFiledOfStudyToScript(ids, index);
                    });
            builder.show();
        });
    }

    private void passSelectedFiledOfStudyToScript(final String[] ids, final int index) {
        web.post(() -> web.loadUrl("javascript:chooseFieldOfStudyById('" + ids[index] + "')"));
    }

    @JavascriptInterface
    public void onServerDataError() {
        Toast.makeText(getContext(), R.string.server_data_error, Toast.LENGTH_LONG).show();
        User user = User.getInstance(getContext());
        if (user.isSaved()) {
            getActivity().finish();
        } else {
            goToLoginFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        web.stopLoading();
        web.destroy();
    }
}
