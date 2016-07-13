package com.marktony.translator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marktony.translator.R;
import com.marktony.translator.interfaze.OnRecyclerViewOnClickListener;
import com.marktony.translator.model.NotebookMarkItem;

import java.util.ArrayList;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class NotebookMarkItemAdapter extends RecyclerView.Adapter<NotebookMarkItemAdapter.ItemViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<NotebookMarkItem> list;
    private OnRecyclerViewOnClickListener mListener;

    public NotebookMarkItemAdapter(Context context, ArrayList<NotebookMarkItem> list){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater.inflate(R.layout.notebook_mark_item,parent,false),mListener);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        NotebookMarkItem item = list.get(position);

        holder.tvInput.setText(item.getInput());
        holder.tvOutput.setText(item.getOutput());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvInput;
        TextView tvOutput;
        ImageView ivMarkStar;
        ImageView ivCopy;
        ImageView ivShare;

        OnRecyclerViewOnClickListener listener;

        public ItemViewHolder(View itemView, final OnRecyclerViewOnClickListener listener) {
            super(itemView);

            tvInput = (TextView) itemView.findViewById(R.id.text_view_input);
            tvOutput = (TextView) itemView.findViewById(R.id.text_view_output);
            ivMarkStar = (ImageView) itemView.findViewById(R.id.image_view_mark_star);
            ivCopy = (ImageView) itemView.findViewById(R.id.image_view_copy);
            ivShare = (ImageView) itemView.findViewById(R.id.image_view_share);

            this.listener = listener;
            itemView.setOnClickListener(this);

            ivMarkStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnSubViewClick(ivMarkStar,getLayoutPosition());
                }
            });

            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnSubViewClick(ivCopy,getLayoutPosition());
                }
            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnSubViewClick(ivShare,getLayoutPosition());
                }
            });

        }

        @Override
        public void onClick(View view) {
            if (listener != null){
                listener.OnItemClick(view,getLayoutPosition());
            }
        }
    }
}
