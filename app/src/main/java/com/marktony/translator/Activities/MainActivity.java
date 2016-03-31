package com.marktony.translator.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.marktony.translator.API.Constants;
import com.marktony.translator.R;
import com.marktony.translator.Utils.NetworkUtil;
import com.marktony.translator.Utils.UTF8Encoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private EditText etInput;
    private TextView tvResult;

    private String input = null;
    private String result = null;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //如果有实体菜单按钮，将其屏蔽，强制显示overflow菜单
        forceShowOverflowMenu();

        queue = Volley.newRequestQueue(getApplicationContext());

        //在这里进行网络连接的判断，如果没有连接，则进行snackbar的提示
        //如果有网络连接，则不会有任何的操作
        if (!NetworkUtil.isNetworkConnected(MainActivity.this)){
            Snackbar.make(fab,R.string.no_network_connected,Snackbar.LENGTH_LONG)
                    .setAction(R.string.setting, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkUtil.isNetworkConnected(MainActivity.this)) {
                    Snackbar.make(fab, R.string.no_network_connected, Snackbar.LENGTH_LONG)
                            .setAction(R.string.setting, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                                }
                            }).show();
                } else if (etInput.getText() == null || etInput.getText().length() == 0) {
                    Snackbar.make(fab, getString(R.string.no_input), Snackbar.LENGTH_SHORT).show();
                } else {

                    sendReq(inputFormat(String.valueOf(etInput.getText())));

                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()){
                    imm.hideSoftInputFromWindow(fab.getWindowToken(),0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {

            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_share){
            if (result != null){
                Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");

                //组合要分享的内容文本
                String shareText = getString(R.string.share_text_part1)
                        + input + "\n"
                        + getString(R.string.share_text_part2)
                        + result
                        + getString(R.string.share_text_part3);

                shareIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                startActivity(Intent.createChooser(shareIntent,getString(R.string.choose_app_to_share)));
            } else {
                Snackbar.make(fab, R.string.no_result_to_share,Snackbar.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        etInput = (EditText) findViewById(R.id.et_main_input);
        tvResult = (TextView) findViewById(R.id.tv_show_result);


    }

    private void sendReq(String in){

        //将传入的in的值赋值给input,这样在share的时候才会有相应的文本
        input = in;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Constants.JUHE_INTERFACE + "?key=" + Constants.JUHE_APPKEY + "&word=" + UTF8Encoder.encode(in),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            switch (jsonObject.getInt("error_code")){

                                case 0:
                                    JSONArray dataArray = jsonObject.getJSONObject("result").getJSONObject("data").getJSONArray("translation");

                                    if (jsonObject.getJSONObject("result").getJSONObject("data").isNull("basic")){
                                        result = dataArray.getString(0);
                                    } else {

                                        JSONObject basicObj = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("basic");
                                        JSONArray basicArray = basicObj.getJSONArray("explains");
                                        String basic = getString(R.string.basic_translation);
                                        for (int i = 0;i < basicArray.length();i++){
                                            basic = basic + "\n[" + (i + 1) + "]" + basicArray.getString(i);
                                        }
                                        result = dataArray.getString(0) + "\n" + basic;
                                    }

                                    tvResult.setText(result);
                                    break;
                                case 211101:
                                    Snackbar.make(fab, R.string.error_cant_analyze,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 211102:
                                    Snackbar.make(fab, R.string.error_too_long,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 211103:
                                    Snackbar.make(fab, R.string.error_only_en_cn,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 211104:
                                    Snackbar.make(fab, R.string.error_no_results,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 211105:
                                    Snackbar.make(fab, R.string.error_wrong_args,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 211106:
                                    Snackbar.make(fab, R.string.error_net_no_result,Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(fab,volleyError.toString(),Snackbar.LENGTH_SHORT).show();
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

    //强制显示overflow菜单
    private void forceShowOverflowMenu(){
        ViewConfiguration config = ViewConfiguration.get(this);
        try {
            Field menuField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuField != null){
                menuField.setAccessible(true);
                menuField.setBoolean(config,false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //去掉输入文本中的回车符号
    private String inputFormat(String in){
        in = in.replace("\n","");
        return in;
    }

}
