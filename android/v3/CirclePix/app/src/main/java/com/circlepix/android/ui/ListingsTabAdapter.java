package com.circlepix.android.ui;

import android.app.Activity;
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

    private static LayoutInflater inflater=null;
    private int selectedPos = 0;

    public ListingsTabAdapter(Activity a, List<ListingInformation> listings) {
        activity = a;
        this.listings = listings;
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


      /*  viewHolder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Updating old as well as new positions
                notifyItemChanged(ListingsTabActivity.selectedPosGlobal);
                ListingsTabActivity.selectedPosGlobal = i;
                notifyItemChanged(ListingsTabActivity.selectedPosGlobal);

                // Do your another stuff for your onClick
                if(ListingsTabActivity.selectedPosGlobal == i){
                    // Here I am just highlighting the background
                    viewHolder.front.setBackgroundColor(Color.GREEN);
                }else{
                    viewHolder.front.setBackgroundColor(Color.WHITE);
                }

                Log.v("im", "here");
            }

        });*/

       /* viewHolder.front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions
                notifyItemChanged(ListingsTabActivity.selectedPosGlobal);
                ListingsTabActivity.selectedPosGlobal = i;
                notifyItemChanged(ListingsTabActivity.selectedPosGlobal);

                // Do your another stuff for your onClick
                if(ListingsTabActivity.selectedPosGlobal == i){
                    // Here I am just highlighting the background
                    viewHolder.front.setBackgroundColor(Color.GREEN);
                }else{
                    viewHolder.front.setBackgroundColor(Color.WHITE);
                }

                Log.v("im", "here");
            }
        });*/


    }
/*
    @Override
    public void onClick(View view) {
        notifyItemChanged(selectedPos);
        selectedPos = getLayoutPosition();
        notifyItemChanged(selectedPos);
    }*/

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemTitle;
        private final TextView itemDesc;
        private final ImageView itemImage;
        private final FrameLayout list_item;
        private final RelativeLayout front;
        private final ProgressBar progressBar;

        ViewHolder(View v) {
            super(v);
            itemTitle = (TextView) v.findViewById(R.id.item_title);
            itemDesc = (TextView) v.findViewById(R.id.item_desc);
            itemImage = (ImageView) v.findViewById(R.id.item_image);
            list_item = (FrameLayout) v.findViewById(R.id.list_row_item);
            front = (RelativeLayout) v.findViewById(R.id.front);
         //   progressBar = new ProgressBar(activity.getApplicationContext(), null, android.R.attr.progressBarStyleSmall);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

         //   list_item.setOnTouchListener(itemTouchListener);

        }

       /* private View.OnTouchListener itemTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.color.colorAccent);//(R.drawable.background_item_event_pressed);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // CANCEL triggers when you press the view for too long
                        // It prevents UP to trigger which makes the 'pressed' background permanent which isn't what we want
                    case MotionEvent.ACTION_OUTSIDE:
                        // OUTSIDE triggers when the user's finger moves out of the view
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundResource(R.color.activity_background); //(R.drawable.background_item_event);
                        break;
                    default:
                        break;
                }

                return true;
            }
        };
*/
    }
}