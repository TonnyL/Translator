package com.marktony.translator.ui;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.ClipboardManager;
import android.view.MenuItem;
import android.view.View;

import com.marktony.translator.R;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();

        findViewById(R.id.layout_bugs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    Uri uri = Uri.parse(getString(R.string.sendto));
                    Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.topic));
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n");
                    startActivity(intent);
                }catch (android.content.ActivityNotFoundException ex){
                    Snackbar.make(toolbar, R.string.no_mail_app,Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.layout_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(AboutActivity.this).create();
                dialog.setTitle(R.string.donate_title);
                dialog.setMessage(getString(R.string.donate_content));
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //将指定账号添加到剪切板
                        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", getString(R.string.donate_account));
                        manager.setPrimaryClip(clipData);
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

            }
        });

        findViewById(R.id.layout_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Uri uri = Uri.parse("market://details?id="+getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex){
                    Snackbar.make(toolbar, R.string.no_app_store,Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.tv_github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.github_url))));
            }
        });

        findViewById(R.id.tv_open_source_licenses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this,OpenSourceLicensesActivity.class));
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}