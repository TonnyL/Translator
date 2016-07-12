package com.marktony.translator.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class DailyOneFragment extends Fragment {

    private RequestQueue queue;

    private TextView textViewEng;
    private TextView textViewChi;
    private ImageView imageViewMain;

    public DailyOneFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_one,container,false);

        initViews(view);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://open.iciba.com/dsapi", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    Glide.with(getActivity())
                            .load(jsonObject.getString("picture2"))
                            .asBitmap()
                            .centerCrop()
                            .error(R.drawable.nav_header)
                            .into(imageViewMain);
                    textViewEng.setText(jsonObject.getString("content"));
                    textViewChi.setText(jsonObject.getString("note"));
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

        return view;
    }

    private void initViews(View view) {

        textViewEng = (TextView) view.findViewById(R.id.text_view_eng);
        textViewChi = (TextView) view.findViewById(R.id.text_view_chi);
        imageViewMain = (ImageView) view.findViewById(R.id.image_view_daily);

    }
}
