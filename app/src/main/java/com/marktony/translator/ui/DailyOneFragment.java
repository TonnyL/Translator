package com.marktony.translator.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.marktony.translator.R;
import com.marktony.translator.db.DBUtil;
import com.marktony.translator.db.NotebookDatabaseHelper;
import com.marktony.translator.util.SnackBarHelper;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class DailyOneFragment extends Fragment {

    private RequestQueue queue;

    private TextView textViewEng;
    private TextView textViewChi;
    private ImageView imageViewMain;
    private ImageView ivStar;
    private ImageView ivCopy;
    private ImageView ivShare;

    private Boolean isMarked = false;

    private NotebookDatabaseHelper dbHelper;

    public DailyOneFragment(){

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
        View view = inflater.inflate(R.layout.fragment_daily_one,container,false);

        initViews(view);

        requestData();

        ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnackBarHelper helper = new SnackBarHelper(getActivity());

                // 在没有被收藏的情况下
                if (!isMarked){
                    ivStar.setImageResource(R.drawable.ic_star_white_24dp);
                    helper.make(ivStar,"战略Mark", Snackbar.LENGTH_SHORT);
                    isMarked = true;

                    ContentValues values = new ContentValues();
                    values.put("input",textViewEng.getText().toString());
                    values.put("output",textViewChi.getText().toString());
                    DBUtil.insertValue(dbHelper,values);

                    values.clear();

                } else {
                    ivStar.setImageResource(R.drawable.ic_star_border_white_24dp);
                    helper.make(ivStar,"取消Mark",Snackbar.LENGTH_SHORT);
                    isMarked = false;

                    DBUtil.deleteValue(dbHelper,textViewEng.getText().toString());

                }

                helper.show();
            }
        });

        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", String.valueOf(textViewEng.getText() + "\n" + textViewChi.getText()));
                manager.setPrimaryClip(clipData);

                SnackBarHelper helper = new SnackBarHelper(getActivity());
                helper.make(ivCopy,"复制成功",Snackbar.LENGTH_SHORT);
                helper.show();
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(textViewEng.getText()) + "\n" + textViewChi.getText());
                startActivity(Intent.createChooser(intent,getString(R.string.choose_app_to_share)));
            }
        });

        return view;
    }

    private void initViews(View view) {

        textViewEng = (TextView) view.findViewById(R.id.text_view_eng);
        textViewChi = (TextView) view.findViewById(R.id.text_view_chi);
        imageViewMain = (ImageView) view.findViewById(R.id.image_view_daily);

        ivStar = (ImageView) view.findViewById(R.id.image_view_mark_star);
        ivCopy = (ImageView) view.findViewById(R.id.image_view_copy);
        ivShare = (ImageView) view.findViewById(R.id.image_view_share);

    }

    private void requestData(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://open.iciba.com/dsapi", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    Glide.with(getActivity())
                            .load(jsonObject.getString("picture2"))
                            .asBitmap()
                            .centerCrop()
                            .into(imageViewMain);

                    textViewEng.setText(jsonObject.getString("content"));
                    textViewChi.setText(jsonObject.getString("note"));

                    if (DBUtil.queryIfItemExist(dbHelper,textViewEng.getText().toString())){
                        ivStar.setImageResource(R.drawable.ic_star_white_24dp);
                        isMarked = true;
                    } else {
                        ivStar.setImageResource(R.drawable.ic_star_border_white_24dp);
                        isMarked = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        queue.add(request);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("config",newConfig.toString());
    }
}
