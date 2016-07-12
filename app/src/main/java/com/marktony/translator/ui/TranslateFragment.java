package com.marktony.translator.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.marktony.translator.R;
import com.marktony.translator.api.Constants;
import com.marktony.translator.util.NetworkUtil;
import com.marktony.translator.util.UTF8Encoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class TranslateFragment extends Fragment {

    private FloatingActionButton fab;
    private EditText etInput;
    private TextView tvClear;
    private ProgressBar progressBar;
    private TextView tvInput;
    private TextView tvOutput;
    private ImageView ivShare;
    private ImageView ivCopy;
    private ImageView ivMark;
    private View incView;

    private String input = null;
    private String result = null;

    private RequestQueue queue;

    // empty constructor required
    public TranslateFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        initViews(view);

        //在这里进行网络连接的判断，如果没有连接，则进行snackbar的提示
        //如果有网络连接，则不会有任何的操作
        if (!NetworkUtil.isNetworkConnected(getActivity())){
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

                if (!NetworkUtil.isNetworkConnected(getActivity())) {
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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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

        return view;
    }

    private void initViews(View view) {

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        etInput = (EditText) view.findViewById(R.id.et_main_input);
        tvClear = (TextView) view.findViewById(R.id.tv_clear);
        // 初始化清除按钮，当没有输入时是不可见的
        tvClear.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        incView = view.findViewById(R.id.include);
        tvInput = (TextView) view.findViewById(R.id.text_view_input);
        tvOutput = (TextView) view.findViewById(R.id.text_view_output);
        incView.setVisibility(View.INVISIBLE);


    }

    private void sendReq(String in){

        progressBar.setVisibility(View.VISIBLE);

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

                                    incView.setVisibility(View.VISIBLE);
                                    tvInput.setText(input);
                                    tvOutput.setText(result);

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

                        progressBar.setVisibility(View.GONE);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Snackbar.make(fab,volleyError.toString(),Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                incView.setVisibility(View.INVISIBLE);
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
