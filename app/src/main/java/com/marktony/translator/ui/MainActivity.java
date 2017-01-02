package com.marktony.translator.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.marktony.translator.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Toolbar toolbar;

    private NoteBookFragment noteBookFragment;
    private DailyOneFragment dailyOneFragment;
    private TranslateFragment translateFragment;

    private static final String ACTION_NOTEBOOK = "com.marktony.translator.notebook";
    private static final String ACTION_DAILY_ONE = "com.marktony.translator.dailyone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();
            noteBookFragment = (NoteBookFragment) manager.getFragment(savedInstanceState, "noteBookFragment");
            dailyOneFragment = (DailyOneFragment) manager.getFragment(savedInstanceState, "dailyOneFragment");
            translateFragment = (TranslateFragment) manager.getFragment(savedInstanceState, "translateFragment");
        } else {
            noteBookFragment = new NoteBookFragment();
            dailyOneFragment = new DailyOneFragment();
            translateFragment = new TranslateFragment();
        }

        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .add(R.id.container_main, translateFragment, "translateFragment")
                .commit();

        manager.beginTransaction()
                .add(R.id.container_main, dailyOneFragment, "dailyOneFragment")
                .commit();

        manager.beginTransaction()
                .add(R.id.container_main, noteBookFragment, "noteBookFragment")
                .commit();

        Intent intent = getIntent();
        if (intent.getAction().equals(ACTION_NOTEBOOK)) {
            showHideFragment(2);
        } else if (intent.getAction().equals(ACTION_DAILY_ONE)){
            showHideFragment(1);
        } else {
            showHideFragment(0);
        }

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
        }
        super.onBackPressed();

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

        if (item.getItemId() == R.id.action_search){
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_translate) {

            showHideFragment(0);

        } else if (id == R.id.nav_daily) {

            showHideFragment(1);

        } else if (id == R.id.nav_notebook) {

            showHideFragment(2);

        } else if (id == R.id.nav_setting) {

            startActivity(new Intent(MainActivity.this,SettingsPreferenceActivity.class));

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (translateFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "translateFragment", translateFragment);
        }

        if (noteBookFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "noteBookFragment", noteBookFragment);
        }

        if (dailyOneFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "dailyOneFragment", dailyOneFragment);
        }

    }

    /**
     * show or hide the fragment
     * and handle other operations like set toolbar's title
     * set the navigation's checked item
     * @param position which fragment to show, only 3 values at this time
     *                 0 for translate fragment
     *                 1 for daily one fragment
     *                 2 for notebook fragment
     */
    private void showHideFragment(@IntRange(from = 0, to = 2) int position) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().hide(translateFragment).commit();
        manager.beginTransaction().hide(noteBookFragment).commit();
        manager.beginTransaction().hide(dailyOneFragment).commit();

        if (position == 0) {
            manager.beginTransaction().show(translateFragment).commit();
            toolbar.setTitle(R.string.app_name);
            navigationView.setCheckedItem(R.id.nav_translate);
        } else if (position == 1) {
            toolbar.setTitle(R.string.daily_one);
            manager.beginTransaction().show(dailyOneFragment).commit();
            navigationView.setCheckedItem(R.id.nav_daily);
        } else if (position == 2) {
            toolbar.setTitle(R.string.notebook);
            manager.beginTransaction().show(noteBookFragment).commit();
            navigationView.setCheckedItem(R.id.nav_notebook);
        }

    }

}
