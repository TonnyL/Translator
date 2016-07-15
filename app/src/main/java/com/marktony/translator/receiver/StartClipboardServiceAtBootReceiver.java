package com.marktony.translator.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.marktony.translator.service.ClipboardService;

/**
 * Created by lizhaotailang on 2016/7/15.
 */

public class StartClipboardServiceAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            SharedPreferences sp = context.getSharedPreferences("settings",Context.MODE_PRIVATE);
            if (sp.getBoolean("enable_clipboard_service",false)){
                Intent serviceIntent = new Intent(context, ClipboardService.class);
                context.startService(serviceIntent);
            }

        }
    }
}
