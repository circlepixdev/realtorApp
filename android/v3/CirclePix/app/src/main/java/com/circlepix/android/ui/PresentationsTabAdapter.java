package com.circlepix.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.circlepix.android.R;
import com.circlepix.android.data.Presentation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by keuahnlumanog1 on 5/27/16.
 */
public class PresentationsTabAdapter extends RecyclerView.Adapter<PresentationsTabAdapter.ViewHolder> {


    private Activity activity;
    private List<Presentation> presentations;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public PresentationsTabAdapter(Activity a,List<Presentation> presentations) {
        activity = a;

        this.presentations = presentations;
        mPref = a.getApplicationContext().getSharedPreferences("presentation", Context.MODE_PRIVATE);
        mEditor = mPref.edit();

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
        viewHolder.itemDesc.setText("Narration: " + presentation.getNarration() + ", Music: " + presentation.getMusic());


        String t = "2014-11-26";

        // To parse input string
        SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        // To format output string
        SimpleDateFormat to = new SimpleDateFormat("MM-dd-yy", Locale.US);

        try {
            String res = to.format(presentation.getModified());
            viewHolder.itemDate.setText(res);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        if (presentations.get(i).isSelected()) {
            viewHolder.list_item.setBackgroundColor(Color.parseColor("#d5d5d5"));
        } else {
            viewHolder.list_item.setBackgroundColor(Color.TRANSPARENT);
        }



//        viewHolder.itemDate.setText("" + presentation.getModified());

//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.YEAR, selectedYear);
//        cal.set(Calendar.MONTH, selectedMonth);
//        cal.set(Calendar.DAY_OF_MONTH, selectedDay);
//        SimpleDateFormat sdf = new SimpleDateFormat();
//        String DATE_FORMAT = "EE, MMM dd, yyyy";
//        sdf.applyPattern(pattern);
//        String formattedDate = sdf.format(cal.getTime());
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
        private final RelativeLayout list_item;

        ViewHolder(View v) {
            super(v);
            itemTitle = (TextView) v.findViewById(R.id.item_title);
            itemDesc = (TextView) v.findViewById(R.id.item_desc);
            itemDate = (TextView) v.findViewById(R.id.item_date);
            itemImage = (ImageView) v.findViewById(R.id.item_image);
            list_item = (RelativeLayout) v.findViewById(R.id.list_row_item);

            v.setClickable(true);
        }
    }



    public static String currentDateTime(String dateInParse){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = null;
        try
        {
            date = sdf.parse(dateInParse);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy");
        String newFormat = formatter.format(date);

        return newFormat ;
    }

    public void setSelected(int pos) {
        try {
            if (presentations.get(pos).isSelected() == true){
                Log.v("Already selected", presentations.get(pos).getName());
                presentations.get(pos).setSelected(false);

            }else{

                if (presentations.size() > 1) {
                    // get previously selected presentation
                    if(presentations.get(mPref.getInt("position", 0)).getName() != null){
                        presentations.get(mPref.getInt("position", 0)).setSelected(false);
                        mEditor.putInt("position", pos);
                        mEditor.commit();
                    }
                }
                presentations.get(pos).setSelected(true);
            }

            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}