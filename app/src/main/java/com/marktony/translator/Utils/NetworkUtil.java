package com.marktony.translator.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lizhaotailang on 2016/2/18.
 */
public class NetworkUtil {

    public static boolean isNetworkConnected(Context context){

        if (context != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                return true;
            }
        }

        return false;
    }

}
