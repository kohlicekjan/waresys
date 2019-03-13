package cz.kohlicek.waresys.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Nástroje pro práci se sítěma
 */
public final class NetworkUtils {

    private NetworkUtils() {

    }

    /**
     * Zjišťuje se jestli je zařízení připojené k síti
     *
     * @param context
     * @return je připojený k síti
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
