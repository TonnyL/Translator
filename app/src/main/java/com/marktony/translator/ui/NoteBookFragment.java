package com.marktony.translator.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marktony.translator.R;
import com.marktony.translator.adapter.NotebookMarkItemAdapter;
import com.marktony.translator.model.NotebookMarkItem;

import java.util.ArrayList;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class NoteBookFragment extends Fragment {

    private RecyclerView recyclerViewNotebook;
    private FloatingActionButton fab;
    private ArrayList<NotebookMarkItem> list = new ArrayList<NotebookMarkItem>();
    private NotebookMarkItemAdapter adapter;

    public NoteBookFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        for (int i=0; i < 5; i++){
            NotebookMarkItem item = new NotebookMarkItem("我是原文" + i,"我是译文" + i);
            list.add(item);
        }

        adapter = new NotebookMarkItemAdapter(getActivity(),list);
        recyclerViewNotebook.setAdapter(adapter);

        return view;
    }

    private void initViews(View view) {

        recyclerViewNotebook = (RecyclerView) view.findViewById(R.id.recycler_view_notebook);
        recyclerViewNotebook.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

    }

}
