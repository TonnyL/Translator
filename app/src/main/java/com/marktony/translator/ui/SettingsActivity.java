package com.marktony.translator.ui;


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
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences("settings",MODE_PRIVATE);
        editor = sp.edit();

        initViews();

        cbTapTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checkbox is checked and service is not running
                if (cbTapTrans.isChecked() && !new ServiceUtil().isMyServiceRunning(SettingsActivity.this,ClipboardService.class)){
                    startService(new Intent(SettingsActivity.this, ClipboardService.class));
                    editor.putBoolean("enable_clipboard_service",true);
                } else if (!cbTapTrans.isChecked() && new ServiceUtil().isMyServiceRunning(SettingsActivity.this,ClipboardService.class)){
                    stopService(new Intent(SettingsActivity.this,ClipboardService.class));
                    editor.putBoolean("enable_clipboard_service",false);
                }

                editor.apply();
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
