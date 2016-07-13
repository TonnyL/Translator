package com.marktony.translator.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
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
import com.marktony.translator.db.DBUtil;
import com.marktony.translator.db.NotebookDatabaseHelper;
import com.marktony.translator.util.NetworkUtil;
import com.marktony.translator.util.SnackBarHelper;
import com.marktony.translator.util.UTF8Encoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.CLIPBOARD_SERVICE;

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

    private NotebookDatabaseHelper dbHelper;

    private String input = null;
    private String result = null;

    private RequestQueue queue;

    private Boolean isMarked = false;

    // empty constructor required
    public TranslateFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        dbHelper = new NotebookDatabaseHelper(getActivity(),"MyStore.db",null,1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        initViews(view);

        //在这里进行网络连接的判断，如果没有连接，则进行snackbar的提示
        //如果有网络连接，则不会有任何的操作
        if (!NetworkUtil.isNetworkConnected(getActivity())){
            showNoNetwork();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    showNoNetwork();
                } else if (etInput.getText() == null || etInput.getText().length() == 0) {
                    SnackBarHelper helper = new SnackBarHelper(getActivity());
                    helper.make(fab, getString(R.string.no_input), Snackbar.LENGTH_SHORT);
                    helper.show();
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

        ivMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SnackBarHelper helper = new SnackBarHelper(getActivity());

                // 在没有被收藏的情况下
                if (!isMarked){
                    ivMark.setImageResource(R.drawable.ic_star_white_24dp);
                    helper.make(fab,"战略Mark",Snackbar.LENGTH_SHORT);
                    isMarked = true;

                    ContentValues values = new ContentValues();
                    values.put("input",input);
                    values.put("output",result);
                    DBUtil.insertValue(dbHelper,values);

                    values.clear();

                } else {
                    ivMark.setImageResource(R.drawable.ic_star_border_white_24dp);
                    helper.make(fab,"取消Mark",Snackbar.LENGTH_SHORT);
                    isMarked = false;

                    DBUtil.deleteValue(dbHelper,input);
                }

                helper.show();
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,result);
                startActivity(Intent.createChooser(intent,getString(R.string.choose_app_to_share)));
            }
        });

        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", result);
                manager.setPrimaryClip(clipData);

                SnackBarHelper helper = new SnackBarHelper(getActivity());
                helper.make(fab,"复制成功",Snackbar.LENGTH_SHORT);
                helper.show();
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
        ivCopy = (ImageView) view.findViewById(R.id.image_view_copy);
        ivMark = (ImageView) view.findViewById(R.id.image_view_mark_star);
        ivShare = (ImageView) view.findViewById(R.id.image_view_share);
        ivMark.setImageResource(R.drawable.ic_star_border_white_24dp);

    }

    private void sendReq(String in){

        progressBar.setVisibility(View.VISIBLE);
        incView.setVisibility(View.INVISIBLE);

        //将传入的in的值赋值给input,这样在share的时候才会有相应的文本
        input = in;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Constants.YOUDAO_URL + "&key=" + Constants.YOUDAO_KEY + "&type=data&doctype=json&version=1.1&q=" + UTF8Encoder.encode(in),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        SnackBarHelper helper = new SnackBarHelper(getActivity());

                        try {
                            switch (jsonObject.getInt("errorCode")){


                                case 0:
                                    // 有道翻译
                                    // 需要进行空值判断
                                    String dic = "";
                                    if (!jsonObject.isNull("translation")){
                                        for (int i = 0;i < jsonObject.getJSONArray("translation").length();i++){
                                            dic = dic + jsonObject.getJSONArray("translation").getString(i);
                                        }
                                    }

                                    result = dic;

                                    incView.setVisibility(View.VISIBLE);
                                    if (DBUtil.queryIfItemExist(dbHelper,input)){
                                        ivMark.setImageResource(R.drawable.ic_star_white_24dp);
                                        isMarked = true;
                                    } else {
                                        ivMark.setImageResource(R.drawable.ic_star_border_white_24dp);
                                        isMarked = false;
                                    }
                                    tvInput.setText(input);
                                    tvOutput.setText(result);

                                    break;
                                case 20:
                                    helper.make(fab,R.string.error_too_long,Snackbar.LENGTH_SHORT);
                                    helper.show();
                                    break;
                                case 30:
                                    helper.make(fab,R.string.unable_to_get_valid_result,Snackbar.LENGTH_SHORT);
                                    helper.show();
                                    break;
                                case 40:
                                    helper.make(fab,R.string.unsupported_language_type,Snackbar.LENGTH_SHORT);
                                    helper.show();
                                    break;
                                case 50:
                                    helper.make(fab,R.string.invalid_key,Snackbar.LENGTH_SHORT);
                                    helper.show();
                                    break;
                                case 60:
                                    helper.make(fab,R.string.no_dic_result,Snackbar.LENGTH_SHORT);
                                    helper.show();
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
                SnackBarHelper helper = new SnackBarHelper(getActivity());
                helper.make(fab,volleyError.toString(),Snackbar.LENGTH_SHORT);
                helper.show();
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

    private void showNoNetwork(){
        SnackBarHelper helper = new SnackBarHelper(getActivity());
        helper.make(fab,R.string.no_network_connected,Snackbar.LENGTH_LONG);
        helper.setAction(R.string.setting, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        helper.show();
    }

}
