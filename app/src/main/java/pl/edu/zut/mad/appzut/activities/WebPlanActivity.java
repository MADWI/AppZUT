package pl.edu.zut.mad.appzut.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.network.DataLoadingManager;
import pl.edu.zut.mad.appzut.network.ScheduleEdzLoader;

public class WebPlanActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_plan_activity);
        final WebView web = (WebView)findViewById(R.id.web_view_plan);
        web.getSettings().setJavaScriptEnabled(true);
        final View pleaseWaitView = findViewById(R.id.please_wait);

        // Inject "/res/raw/grab_schedule.js" on all pages
        web.setWebViewClient(new WebViewClient(){
            private String mReceivedErrorForUrl;

            @Override
            public void onPageFinished(WebView view, String url) {
                // Add to page script from "/appwizut-injected-script.js" (replaced below)
                web.loadUrl("javascript:javascript:(function(){document.body.appendChild(document.createElement('script')).src='/appwizut-injected-script.js'})()");
            }

            @Override
            @SuppressWarnings("deprecation") // Newer version is not supported on older Android versions
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                // Load contents of "/appwizut-injected-script.js" from resource
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

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Show or hide "please wait" text
                // Hide on login page
                boolean shouldHidePleaseWait = url.contains("Logowanie2.aspx");

                // Hide if on error page
                if (mReceivedErrorForUrl != null) {
                    if (mReceivedErrorForUrl.equals(url)) {
                        shouldHidePleaseWait = true;
                    } else {
                        mReceivedErrorForUrl = null;
                    }
                }

                pleaseWaitView.setVisibility(shouldHidePleaseWait ? View.GONE : View.VISIBLE);
            }

            @Override
            @SuppressWarnings("deprecation") // Newer version is not supported on older Android versions
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Hide "please wait" text
                pleaseWaitView.setVisibility(View.GONE);
                mReceivedErrorForUrl = failingUrl;
            }
        });

        web.loadUrl("https://edziekanat.zut.edu.pl/WU/PodzGodzin.aspx");
    }

    public void tableGrabbedByJavascript(final String contents) {
        DataLoadingManager
                .getInstance(WebPlanActivity.this)
                .getLoader(ScheduleEdzLoader.class)
                .setSourceTableJson(contents);
        finish();
    }
}
