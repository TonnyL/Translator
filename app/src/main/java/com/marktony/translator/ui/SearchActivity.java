package com.marktony.translator.ui;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marktony.translator.R;
import com.marktony.translator.adapter.NotebookMarkItemAdapter;
import com.marktony.translator.db.DBUtil;
import com.marktony.translator.db.NotebookDatabaseHelper;
import com.marktony.translator.interfaze.OnRecyclerViewOnClickListener;
import com.marktony.translator.model.NotebookMarkItem;
import com.marktony.translator.util.SnackBarHelper;

import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private SearchView.OnQueryTextListener listener;

    private RecyclerView recyclerView;
    private TextView textView;
    private ProgressBar progressBar;

    private NotebookMarkItemAdapter adapter;

    private ArrayList<NotebookMarkItem> list = new ArrayList<NotebookMarkItem>();

    private NotebookDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new NotebookDatabaseHelper(SearchActivity.this,"MyStore.db",null,1);
        db = dbHelper.getReadableDatabase();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_notebook);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        textView = (TextView) findViewById(R.id.text_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (item != null){
            searchView = (SearchView) item.getActionView();
        }

        if (searchView != null){
            searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            listener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    if (!list.isEmpty()){
                        list.clear();
                        recyclerView.removeAllViews();
                        textView.setVisibility(View.GONE);
                    }

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {

                    progressBar.setVisibility(View.VISIBLE);

                    Cursor cursor = db.query("notebook",null,null,null,null,null,null);
                    if (cursor.moveToFirst()){
                        do {
                            String in = cursor.getString(cursor.getColumnIndex("input"));
                            String out = cursor.getString(cursor.getColumnIndex("output"));

                            if (in.contains(query) || out.contains(query)){
                                NotebookMarkItem item1 = new NotebookMarkItem(in,out);
                                list.add(item1);
                            }

                        } while (cursor.moveToNext());
                    }

                    cursor.close();

                    if (list.isEmpty()){
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.GONE);
                        handleResults();
                    }

                    progressBar.setVisibility(View.GONE);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(listener);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void handleResults() {

        Collections.reverse(list);
        adapter = new NotebookMarkItemAdapter(SearchActivity.this,list);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {

            @Override
            public void OnItemClick(View view, int position) {

            }

            @Override
            public void OnSubViewClick(View view, final int position) {

                switch (view.getId()){

                    case R.id.image_view_share:

                        NotebookMarkItem item1 = list.get(position);

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,String.valueOf(item1.getInput() + "\n" + item1.getOutput()));
                        startActivity(Intent.createChooser(intent,getString(R.string.choose_app_to_share)));

                        break;

                    case R.id.image_view_copy:

                        NotebookMarkItem item2 = list.get(position);

                        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", String.valueOf(item2.getInput() + "\n" + item2.getOutput()));
                        manager.setPrimaryClip(clipData);

                        SnackBarHelper helper = new SnackBarHelper(SearchActivity.this);
                        helper.make(recyclerView,getString(R.string.copy_done), Snackbar.LENGTH_SHORT);
                        helper.show();

                        break;

                    case R.id.image_view_mark_star:

                        final NotebookMarkItem item3 = list.get(position);

                        DBUtil.deleteValue(dbHelper,item3.getInput());

                        list.remove(position);

                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position,list.size());

                        SnackBarHelper h = new SnackBarHelper(SearchActivity.this);
                        h.make(recyclerView,getString(R.string.add_to_notebook),Snackbar.LENGTH_LONG);
                        h.setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ContentValues values = new ContentValues();
                                values.put("input",item3.getInput());
                                values.put("output",item3.getOutput());

                                DBUtil.insertValue(dbHelper,values);

                                values.clear();

                                list.add(position,item3);
                                adapter.notifyItemInserted(position);
                                recyclerView.smoothScrollToPosition(position);

                            }
                        });
                        h.show();

                        break;

                    default:
                        break;
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        if (id == R.id.action_search){
            return false;
        }

        searchView.setOnQueryTextListener(listener);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(),0);
        }
    }
}
