package com.circlepix.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.ListingsTabActivity;
import com.circlepix.android.R;
import com.circlepix.android.beans.Listing;
import com.circlepix.android.beans.ListingInformation;

import java.util.List;

/**
 * Created by keuahnlumanog1 on 5/18/16.
 */
public class ListingsTabAdapter extends RecyclerView.Adapter<ListingsTabAdapter.ViewHolder> {


    private Activity activity;
//    private List<Listing> listings;
    private List<ListingInformation> listings;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private static LayoutInflater inflater=null;
    private int selectedPos = 0;

    public ListingsTabAdapter(Activity a, List<ListingInformation> listings) {
        activity = a;
        this.listings = listings;
        mPref = a.getApplicationContext().getSharedPreferences("listing", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listings_list_row, viewGroup, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ListingInformation listing = listings.get(i);
        viewHolder.itemTitle.setText("" + listing.getAddressLine1());
        viewHolder.itemDesc.setText("" + listing.getAddressLine2());

        if(!listing.getImage().isEmpty()){
            Glide.with(activity.getApplicationContext())
                    .load(listing.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.circlepix_bg)
                    .error(R.drawable.broken_file_icon)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewHolder.itemImage);
        }else{
            Glide.with(activity.getApplicationContext()).load("").placeholder(R.drawable.circlepix_bg).into(viewHolder.itemImage);
            viewHolder.progressBar.setVisibility(View.GONE);
        }


        if (listings.get(i).isSelected()) {
            viewHolder.list_item.setBackgroundColor(Color.parseColor("#d5d5d5"));
        } else {
            viewHolder.list_item.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemTitle;
        private final TextView itemDesc;
        private final ImageView itemImage;
        private final RelativeLayout list_item;
        private final ProgressBar progressBar;

        ViewHolder(View v) {
            super(v);
            itemTitle = (TextView) v.findViewById(R.id.item_title);
            itemDesc = (TextView) v.findViewById(R.id.item_desc);
            itemImage = (ImageView) v.findViewById(R.id.item_image);
            list_item = (RelativeLayout) v.findViewById(R.id.list_row_item);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

            v.setClickable(true);
        }

    }

    public void setSelected(int pos) {
        try {
            if (listings.get(pos).isSelected() == true){
                Log.v("Already selected", listings.get(pos).getCity());

                listings.get(pos).setSelected(false);

            }else{
                if (listings.size() > 1) {

                    listings.get(mPref.getInt("position", 0)).setSelected(false);
                    mEditor.putInt("position", pos);
                    mEditor.commit();
                }
                listings.get(pos).setSelected(true);
            }

            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}