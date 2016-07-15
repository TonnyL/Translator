package com.marktony.translator.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.marktony.translator.R;
import com.marktony.translator.api.Constants;
import com.marktony.translator.util.UTF8Encoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhaotailang on 2016/7/14.
 */

public class ClipboardService extends Service {

    private ClipboardManager manager;

    private RequestQueue queue;

    private static final String TAG = ClipboardService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        manager.addPrimaryClipChangedListener(listener);

        queue = Volley.newRequestQueue(ClipboardService.this.getApplicationContext());

    }

    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {

            if (manager.hasPrimaryClip()){

                ClipData data = manager.getPrimaryClip();

                handleClipData(data.getItemAt(0).getText().toString());

            }

        }
    };

    private void handleClipData(final String clipData) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Constants.YOUDAO_URL + "&key=" + Constants.YOUDAO_KEY + "&type=data&doctype=json&version=1.1&q=" + UTF8Encoder.encode(clipData),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            switch (jsonObject.getInt("errorCode")){


                                case 0:

                                    // 需要进行空值判断
                                    String dic = clipData + "\n";
                                    if (!jsonObject.isNull("translation")){
                                        for (int i = 0;i < jsonObject.getJSONArray("translation").length();i++){
                                            dic = dic + jsonObject.getJSONArray("translation").getString(i);
                                        }

                                        dic = dic + "\n";
                                    }

                                    if (!jsonObject.isNull("basic")){
                                        dic = dic + getString(R.string.pronunciation) + jsonObject.getJSONObject("basic").getString("phonetic") + "\n";
                                        for (int i = 0; i < jsonObject.getJSONObject("basic").getJSONArray("explains").length(); i++){
                                            dic = dic + jsonObject.getJSONObject("basic").getJSONArray("explains").getString(i) + "; ";
                                        }
                                    }

                                    NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ClipboardService.this)
                                            .setSmallIcon(R.drawable.ic_small_icon)
                                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                                            .setContentTitle(getString(R.string.app_name))
                                            .setContentText(dic)
                                            .setWhen(System.currentTimeMillis())
                                            .setPriority(Notification.PRIORITY_DEFAULT)
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(dic));

                                    Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT,dic);

                                    PendingIntent sharePi = PendingIntent.getActivity(ClipboardService.this,0,shareIntent,0);

                                    mBuilder.addAction(R.drawable.ic_share,getString(R.string.share),sharePi);

                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    manager.notify(0,mBuilder.build());


                                    break;
                                case 20:
                                    Toast.makeText(ClipboardService.this,R.string.error_too_long,Toast.LENGTH_SHORT).show();
                                    break;
                                case 30:
                                    Toast.makeText(ClipboardService.this,R.string.unable_to_get_valid_result,Toast.LENGTH_SHORT).show();
                                    break;
                                case 40:
                                    Toast.makeText(ClipboardService.this,R.string.unsupported_language_type,Toast.LENGTH_SHORT).show();
                                    break;
                                case 50:
                                    Toast.makeText(ClipboardService.this,R.string.invalid_key,Toast.LENGTH_SHORT).show();
                                    break;
                                case 60:
                                    Toast.makeText(ClipboardService.this,R.string.no_dic_result,Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    break;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String,String> headers = new HashMap<String, String>();
                headers.put("Accept","application/json");
                headers.put("Content-Type","application/json,charset=UTF-8");
                return headers;
            }
        };

        queue.add(request);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null){
            manager.removePrimaryClipChangedListener(listener);
        }
    }

}
