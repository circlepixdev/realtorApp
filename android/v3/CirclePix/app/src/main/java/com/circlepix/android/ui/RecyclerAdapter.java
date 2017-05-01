package com.circlepix.android.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.circlepix.android.R;

import java.util.List;

/**
 * Created by keuahnlumanog1 on 4/20/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    List<String> mItems;

    public RecyclerAdapter(List<String> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_list_row, viewGroup, false);

        return new ViewHolder(v);
    }

//    @Override
//    public void onBindViewHolder(ViewHolder viewHolder, int i) {
//        String item = mItems.get(i);
//        viewHolder.mTextView.setText(item);
//    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String item = mItems.get(i);
        viewHolder.mTextView.setText(item);
//
//        View.OnClickListener mClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ViewHolder holder = (ViewHolder) view.getTag();
//                int position = holder.getAdapterPosition();
//
//                Context context = view.getContext();
//                context.startActivity(new Intent(context, LeadSelectedActivity.class));
//
//               // startAppointmentBookingFor(mCategories.get(position));
//            }
//        };
//
//        viewHolder.list_item.setOnClickListener(mClickListener);
//        viewHolder.list_item.setTag(viewHolder);
//
//        viewHolder.list_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Context context = view.getContext();
//                context.startActivity(new Intent(context, LeadSelectedActivity.class));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final RelativeLayout list_item;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.item_title);
            list_item = (RelativeLayout) v.findViewById(R.id.list_row_item);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "onClick：" + getAdapterPosition());
                }
            });
        }
    }

}
// Used to cache the views within the item layout for fast access
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private final TextView mTextView;
//        private final FrameLayout list_item;
//        private Context context;
//
//        public ViewHolder(Context context, View v) {
//            super(v);
//            this.mTextView = (TextView) v.findViewById(R.id.item_title);
//            this.list_item = (FrameLayout) v.findViewById(R.id.list_item);
//            // Store the context
//            this.context = context;
//            // Attach a click listener to the entire row view
//            itemView.setOnClickListener(this);
//        }
//
//        // Handles the row being being clicked
//        @Override
//        public void onClick(View view) {
//            int position = getLayoutPosition(); // gets item position
//
//            context = view.getContext();
//            context.startActivity(new Intent(context, LeadSelectedActivity.class));
////            User user = users.get(position);
////            // We can access the data within the views
////            Toast.makeText(context, tvName.getText(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // ...
//}


//Other method
//    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private final TextView mTextView;
//        private final FrameLayout list_item;
//
//        public ViewHolder(View v) {
//            super(v);
//            mTextView = (TextView) v.findViewById(R.id.item_title);
//            list_item = (FrameLayout) v.findViewById(R.id.list_item);
//
//            list_item.setOnClickListener(this);
//
//        }
//
//        @Override
//        public void onClick(View view) {
//            int position = getAdapterPosition();
//
//
//            switch (view.getId()) {
//                case R.id.list_item:
//
//                    Log.v("Selected position: ", String.valueOf(position));
//                    break;
//            }
//        }
//    }

//}