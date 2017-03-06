package pl.edu.zut.mad.appzut.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class HttpConnect {

    private HttpConnect() {
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
