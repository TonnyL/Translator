package com.marktony.translator.Activities;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.marktony.translator.R;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvThanks;
    private TextView tvFeedback;
    private TextView tvDonate;
    private TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();

        tvThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder dialog = new MaterialDialog.Builder(AboutActivity.this);
                dialog.title(R.string.thanksto);
                dialog.content(R.string.thanksto_content);
                dialog.neutralText(R.string.got_it);
                dialog.onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getString(R.string.sendto));
                Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.topic));
                intent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.phone_modle) + Build.MODEL
                        + "\n" + getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n");
                startActivity(intent);
            }
        });

        tvDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder  dialog = new MaterialDialog.Builder(AboutActivity.this);
                dialog.title(R.string.donate_title);
                dialog.content(R.string.donate_content);
                dialog.positiveText(R.string.OK);
                dialog.negativeText(R.string.cancle);
                dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        //将指定账号添加到剪切板
                        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", getString(R.string.donate_account));
                        manager.setPrimaryClip(clipData);

                        dialog.dismiss();
                    }
                });
                dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        tvScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("market://details?id="+getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvThanks = (TextView) findViewById(R.id.tv_thanks);
        tvFeedback  = (TextView) findViewById(R.id.tv_feedback);
        tvDonate = (TextView) findViewById(R.id.tv_donate);
        tvScore = (TextView) findViewById(R.id.tv_score);

    }

}