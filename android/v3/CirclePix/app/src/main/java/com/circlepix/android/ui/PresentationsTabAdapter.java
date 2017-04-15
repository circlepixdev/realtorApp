package com.circlepix.android.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.circlepix.android.R;
import com.circlepix.android.data.Presentation;

import java.util.List;

/**
 * Created by keuahnlumanog1 on 5/27/16.
 */
public class PresentationsTabAdapter extends RecyclerView.Adapter<PresentationsTabAdapter.ViewHolder> {


    private Activity activity;
    private List<Presentation> presentations;

    public PresentationsTabAdapter(Activity a,List<Presentation> presentations) {
        activity = a;
        this.presentations = presentations;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_presentations, viewGroup, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Presentation presentation = presentations.get(i);
        viewHolder.itemTitle.setText("" + presentation.getName());
        viewHolder.itemDesc.setText("Narration: " + presentation.getNarration() + " Music: " + presentation.getMusic());
        viewHolder.itemDate.setText("" + presentation.getModified());
    }

    @Override
    public int getItemCount() {
        return presentations.size();
    }

    public long getItemId(int position) {
        return presentations.get(position).hashCode();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemTitle;
        private final TextView itemDesc;
        private final TextView itemDate;
        private final ImageView itemImage;
        private final FrameLayout list_item;

        ViewHolder(View v) {
            super(v);
            itemTitle = (TextView) v.findViewById(R.id.item_title);
            itemDesc = (TextView) v.findViewById(R.id.item_desc);
            itemDate = (TextView) v.findViewById(R.id.item_date);
            itemImage = (ImageView) v.findViewById(R.id.item_image);
            list_item = (FrameLayout) v.findViewById(R.id.list_row_item);


        }
    }
}