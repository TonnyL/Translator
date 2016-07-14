package com.marktony.translator.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.marktony.translator.R;
import com.marktony.translator.service.ClipboardService;
import com.marktony.translator.util.ServiceUtil;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox cbTapTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();

        cbTapTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbTapTrans.isChecked()){
                    startService(new Intent(SettingsActivity.this, ClipboardService.class));
                } else if (!cbTapTrans.isChecked()){
                    stopService(new Intent(SettingsActivity.this,ClipboardService.class));
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cbTapTrans = (CheckBox) findViewById(R.id.cb_tap_trans);
        cbTapTrans.setChecked(new ServiceUtil().isMyServiceRunning(SettingsActivity.this,ClipboardService.class));
    }
}
