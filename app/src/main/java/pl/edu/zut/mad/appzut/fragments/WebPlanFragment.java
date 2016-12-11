package pl.edu.zut.mad.appzut.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;

public class WebPlanFragment extends Fragment {

    @BindView(R.id.web_view_plan)
    WebView web;

    @BindView(R.id.please_wait)
    View pleaseWaitView;

    public static final String USERNAME_ARG = "username";
    public static final String PASSWORD_ARG = "password";

    private String username;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_plan, container, false);
        ButterKnife.bind(this, view);
        username = getArguments().getString(USERNAME_ARG);
        password = getArguments().getString(PASSWORD_ARG);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWebView();
    }

    private void initWebView() {
        pleaseWaitView.setVisibility(View.VISIBLE);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(this, "android");
        web.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                web.loadUrl("javascript:javascript:(function(){document.body.appendChild(document.createElement('script')).src='/appwizut-injected-script.js'})()");
                web.loadUrl("javascript:var x =document.getElementById('ctl00_ctl00_ContentPlaceHolder_MiddleContentPlaceHolder_txtIdent').value = '"+username+"';");
                web.loadUrl("javascript:var y =document.getElementById('ctl00_ctl00_ContentPlaceHolder_MiddleContentPlaceHolder_txtHaslo').value = '"+password+"';");
                web.loadUrl("javascript:var z =document.getElementById('ctl00_ctl00_ContentPlaceHolder_MiddleContentPlaceHolder_butLoguj').click();");
                web.loadUrl("javascript:android.onLoginError(document.getElementById('ctl00_ctl00_ContentPlaceHolder_MiddleContentPlaceHolder_lblMessage').innerHTML)");
            }

            @Override
            @SuppressWarnings("deprecation") // Newer version is not supported on older Android versions
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

            // Handle response from javascript
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("js-grabbed-table:")) {
                    String tableJson;
                    try {
                        tableJson = URLDecoder.decode(url.substring(17), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    tableGrabbedByJavascript(tableJson);
                    return true;
                }
                return false;
            }
        });

        web.loadUrl("https://edziekanat.zut.edu.pl/WU/PodzGodzin.aspx");
    }

    public void tableGrabbedByJavascript(final String contents) {
        DataLoadingManager
                .getInstance(getContext())
                .getLoader(ScheduleEdzLoader.class)
                .setSourceTableJson(contents);
        getActivity().finish();
    }

    @JavascriptInterface
    public void onLoginError(String error) {
        if(error != null) {
            if(!error.equals(getResources().getString(R.string.incorrect_error))) {
                web.post(new Runnable() {
                    @Override
                    public void run() {
                        web.stopLoading();
                        web.destroy();
                    }
                });
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                Fragment f = new LoginFragment();
                getFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        web.stopLoading();
        web.destroy();
    }
}
