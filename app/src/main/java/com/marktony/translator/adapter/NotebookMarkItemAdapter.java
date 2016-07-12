package com.marktony.translator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marktony.translator.R;
import com.marktony.translator.model.NotebookMarkItem;

import java.util.ArrayList;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class NotebookMarkItemAdapter extends RecyclerView.Adapter<NotebookMarkItemAdapter.ItemViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<NotebookMarkItem> list;

    public NotebookMarkItemAdapter(Context context, ArrayList<NotebookMarkItem> list){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater.inflate(R.layout.notebook_mark_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        NotebookMarkItem item = list.get(position);

        holder.tvInput.setText(item.getInput());
        holder.tvOutput.setText(item.getOutput());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvInput;
        TextView tvOutput;
        ImageView ivMarkStar;
        ImageView ivCopy;
        ImageView ivShare;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvInput = (TextView) itemView.findViewById(R.id.text_view_input);
            tvOutput = (TextView) itemView.findViewById(R.id.text_view_output);
            ivMarkStar = (ImageView) itemView.findViewById(R.id.image_view_mark_star);
            ivCopy = (ImageView) itemView.findViewById(R.id.image_view_copy);
            ivShare = (ImageView) itemView.findViewById(R.id.image_view_share);

        }
    }
}
