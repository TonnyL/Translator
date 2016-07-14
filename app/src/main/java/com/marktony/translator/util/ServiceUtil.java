package com.marktony.translator.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by lizhaotailang on 2016/7/14.
 */

public class ServiceUtil {

    public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
