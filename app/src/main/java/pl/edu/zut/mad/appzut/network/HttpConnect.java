package pl.edu.zut.mad.appzut.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;


public class HttpConnect {

    private HttpConnect(String address) {
        try {
            URL url = new URL(address);
            HttpURLConnection mUrlConnection = (HttpURLConnection) url.openConnection();
            mUrlConnection.setConnectTimeout(5000);
            mUrlConnection.setReadTimeout(5000);
        } catch (Exception e) {
            // Can only fail with invalid argument, not due to network problem
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether internet connection is turned on.
     * Note: if this returns true, it doesn't mean that connection will succeed
     *       (through if it's false, there's no point in trying to connect).
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() && ni.isConnected();
    }

}
