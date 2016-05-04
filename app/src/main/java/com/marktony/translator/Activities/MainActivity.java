package com.marktony.translator.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private EditText etInput;
    private TextView tvResult;
    private TextView tvClear;

    private String input = null;
    private String result = null;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

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

                // 监听输入面板的情况，如果激活则隐藏
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()){
                    imm.hideSoftInputFromWindow(fab.getWindowToken(),0);
                }
            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count != 0){

                    tvClear.setVisibility(View.VISIBLE);

                    tvClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etInput.setText("");
                        }
                    });
                } else {
                    tvClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        tvClear = (TextView) findViewById(R.id.tv_clear);
        // 初始化清除按钮，当没有输入时是不可见的
        tvClear.setVisibility(View.INVISIBLE);

    }

    private void sendReq(String in){

        //将传入的in的值赋值给input,这样在share的时候才会有相应的文本
        input = in;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Constants.YOUDAO_URL + "&key=" + Constants.YOUDAO_KEY + "&type=data&doctype=json&version=1.1&q=" + UTF8Encoder.encode(in),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            switch (jsonObject.getInt("errorCode")){

                                case 0:
                                    // 有道翻译
                                    // 需要进行空值判断
                                    String dic = "";
                                    if (!jsonObject.isNull("translation")){
                                        for (int i = 0;i < jsonObject.getJSONArray("translation").length();i++){
                                            dic = dic + jsonObject.getJSONArray("translation").getString(i) + "\n";
                                        }
                                    }

                                    // 有道词典基本释义
                                    // 需要进行空值判断
                                    String basic = "";
                                    if ( !jsonObject.isNull("basic")){
                                        for (int i = 0;i < jsonObject.getJSONObject("basic").getJSONArray("explains").length();i++){
                                            basic = basic + jsonObject.getJSONObject("basic").getJSONArray("explains").getString(i) + ";";
                                        }
                                    }

                                    result = getString(R.string.translation) + dic + getString(R.string.basic_meaning) + basic;
                                    tvResult.setText(result);

                                    break;
                                case 20:
                                    Snackbar.make(fab, R.string.error_too_long,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 30:
                                    Snackbar.make(fab, R.string.unable_to_get_valid_result,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 40:
                                    Snackbar.make(fab, R.string.unsupported_language_type,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 50:
                                    Snackbar.make(fab, R.string.invalid_key,Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 60:
                                    Snackbar.make(fab, R.string.no_dic_result,Snackbar.LENGTH_SHORT).show();
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

    //去掉输入文本中的回车符号
    private String inputFormat(String in){
        in = in.replace("\n","");
        return in;
    }

}
