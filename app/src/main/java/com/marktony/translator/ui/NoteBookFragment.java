package com.marktony.translator.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.marktony.translator.R;
import com.marktony.translator.adapter.NotebookMarkItemAdapter;
import com.marktony.translator.db.DBUtil;
import com.marktony.translator.db.NotebookDatabaseHelper;
import com.marktony.translator.interfaze.OnRecyclerViewOnClickListener;
import com.marktony.translator.model.NotebookMarkItem;
import com.marktony.translator.util.SnackBarHelper;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class NoteBookFragment extends Fragment {

    private RecyclerView recyclerViewNotebook;
    private FloatingActionButton fab;
    private ArrayList<NotebookMarkItem> list = new ArrayList<NotebookMarkItem>();
    private NotebookMarkItemAdapter adapter;

    private NotebookDatabaseHelper dbHelper;

    public NoteBookFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new NotebookDatabaseHelper(getActivity(),"MyStore.db",null,1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notebook,container,false);

        initViews(view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(fab,"fab",Snackbar.LENGTH_SHORT).show();
            }
        });

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("notebook",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String in = cursor.getString(cursor.getColumnIndex("input"));
                String out = cursor.getString(cursor.getColumnIndex("output"));

                NotebookMarkItem item = new NotebookMarkItem(in,out);
                list.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter = new NotebookMarkItemAdapter(getActivity(),list);
        recyclerViewNotebook.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {



            @Override
            public void OnItemClick(View view, int position) {

            }

            @Override
            public void OnSubViewClick(View view, int position) {

                NotebookMarkItem item = list.get(position);

                switch (view.getId()){
                    case R.id.image_view_share:

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,String.valueOf(item.getInput() + "\n" + item.getOutput()));
                        startActivity(Intent.createChooser(intent,getString(R.string.choose_app_to_share)));

                        break;

                    case R.id.image_view_copy:

                        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", String.valueOf(item.getInput() + "\n" + item.getOutput()));
                        manager.setPrimaryClip(clipData);

                        SnackBarHelper helper = new SnackBarHelper(getActivity());
                        helper.make(fab,"复制成功",Snackbar.LENGTH_SHORT);
                        helper.show();

                        break;

                    case R.id.image_view_mark_star:

                        DBUtil.deleteValue(dbHelper,item.getInput());

                        final NotebookMarkItem i = list.get(position);

                        list.remove(position);

                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position,list.size());

                        SnackBarHelper h = new SnackBarHelper(getActivity());
                        h.make(fab,"取消Mark",Snackbar.LENGTH_LONG);
                        h.setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ContentValues values = new ContentValues();
                                values.put("input",i.getInput());
                                values.put("output",i.getOutput());
                                DBUtil.insertValue(dbHelper,values);

                                values.clear();

                                list.add(i);
                                adapter.notifyItemInserted(0);
                            }
                        });
                        h.show();

                        break;

                    default:
                        break;
                }
            }
        });

        return view;
    }

    private void initViews(View view) {

        recyclerViewNotebook = (RecyclerView) view.findViewById(R.id.recycler_view_notebook);
        recyclerViewNotebook.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

    }

}
