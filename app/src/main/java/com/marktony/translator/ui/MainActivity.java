package com.marktony.translator.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.marktony.translator.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        navigationView.setCheckedItem(R.id.nav_translate);
        change2Fragment(new TranslateFragment());

    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
                    .setSmallIcon(R.drawable.ic_small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("通知内容")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("通知内容"));

            Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,"通知内容");

            PendingIntent sharePi = PendingIntent.getActivity(MainActivity.this,0,shareIntent,PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent copyPi = PendingIntent.getActivity(MainActivity.this,1,new Intent().setAction(Intent.ACTION_SEND),PendingIntent.FLAG_CANCEL_CURRENT);

            //intentShare.send(TranslateActivity.this,1,);

            mBuilder.addAction(R.drawable.ic_copy,"复制",copyPi)
                    .addAction(R.drawable.ic_share,"分享",sharePi);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0,mBuilder.build());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_translate) {

            toolbar.setTitle(R.string.app_name);
            change2Fragment(new TranslateFragment());

        } else if (id == R.id.nav_daily) {

            toolbar.setTitle(R.string.daily_one);
            change2Fragment(new DailyOneFragment());

        } else if (id == R.id.nav_notebook) {

            toolbar.setTitle(R.string.notebook);
            change2Fragment(new NoteBookFragment());

        } else if (id == R.id.nav_setting) {


        } else if (id == R.id.nav_about) {

            startActivity(new Intent(MainActivity.this,AboutActivity.class));

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void change2Fragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container_main,fragment).commit();
    }

}
