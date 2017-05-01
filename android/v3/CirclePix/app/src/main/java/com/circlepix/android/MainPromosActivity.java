package com.circlepix.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.beans.AgentData;
import com.circlepix.android.helpers.CropCircleTransformation;

/**
 * Created by Keuahn on 8/27/2016.
 */
public class MainPromosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_promos);
    }


    public static class PlaceholderFragment extends Fragment {
        // Store instance variables
        private AgentData agentData;
        private ViewPager viewPager;
        private MyViewPagerAdapter myViewPagerAdapter;
        private LinearLayout dotsLayout;
        private TextView[] dots;
        private int[] layouts;
        private Button btnPrev, btnNext;
        private View mCurrentView;

        // newInstance constructor for creating fragment with arguments
        public static PlaceholderFragment newInstance(int tabPosition) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            return fragment;
        }

        // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Get global shared data
            agentData = AgentData.getInstance();

        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v =  inflater.inflate(R.layout.fragment_main_promos, container, false);

            viewPager = (ViewPager) v.findViewById(R.id.view_pager);
            dotsLayout = (LinearLayout) v.findViewById(R.id.layoutDots);
            btnPrev = (Button) v.findViewById(R.id.btn_skip);
            btnNext = (Button) v.findViewById(R.id.btn_next);

            // layouts of all welcome sliders
            // add few more layouts if you want
            layouts = new int[]{
                    R.layout.promo_slide1//,
                 //   R.layout.promo_slide2,
                 //   R.layout.promo_slide3,
                 //   R.layout.promo_slide4
            };

            // adding bottom dots
            //addBottomDots(0);

            myViewPagerAdapter = new MyViewPagerAdapter();
            viewPager.setAdapter(myViewPagerAdapter);

            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        /*    btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  launchHomeScreen();
                    int current = getItem(-1);
                    if(current < layouts.length){
                        viewPager.setCurrentItem(current);
                    }

                    if(current == 1){
                        // move to next screen
                       viewPager.setCurrentItem(current);

                       // Get the imageview id in current viewpager and set image in imageview
                       View currentView = mCurrentView;
                       ImageView currentImageView = (ImageView) currentView.findViewById(R.id.agent_img);
                       final ProgressBar progressBar = (ProgressBar) currentView.findViewById(R.id.progressBar);

                           if(!agentData.getRealtor().getImage().equals("")){
                               Glide.with(getActivity().getApplicationContext())
                                       .load(agentData.getRealtor().getImage())
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
                                               progressBar.setVisibility(View.GONE);
                                               return false;
                                           }
                                       })
                                       .bitmapTransform(new CropCircleTransformation(getActivity().getApplicationContext()))
                                       .into(currentImageView);
                           }else{
                               currentImageView.setImageResource(R.drawable.avatar);
                               progressBar.setVisibility(View.GONE);
                           }
                       }
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // checking for last page
                    // if last page home screen will be launched
                    int current = getItem(+1);
                    if (current == 0) {
                        btnNext.setText(getString(R.string.next));
                        btnPrev.setVisibility(View.GONE);
                    }else  if(current == 1){
                        // move to next screen
                        viewPager.setCurrentItem(current);
                        btnNext.setText(getString(R.string.next));
                        btnNext.setVisibility(View.VISIBLE);
                        btnPrev.setVisibility(View.VISIBLE);

                        View currentView = mCurrentView;
                        ImageView currentImageView = (ImageView) currentView.findViewById(R.id.agent_img);
                        final ProgressBar progressBar = (ProgressBar) currentView.findViewById(R.id.progressBar);
                      //  BitmapDrawable bd = (BitmapDrawable) currentImageView.getDrawable();
                      //  Bitmap bmp = bd.getBitmap();

                            if(!agentData.getRealtor().getImage().equals("")){
                                Glide.with(getActivity().getApplicationContext())
                                        .load(agentData.getRealtor().getImage())
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
                                                progressBar.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .bitmapTransform(new CropCircleTransformation(getActivity().getApplicationContext()))
                                        .into(currentImageView);
                            }else{
                            currentImageView.setImageResource(R.drawable.avatar);
                            progressBar.setVisibility(View.GONE);
                            }
                    }else if (current != 0 && current < layouts.length-1) {
                        // move to next screen
                        viewPager.setCurrentItem(current);
                        btnNext.setText(getString(R.string.next));
                        btnNext.setVisibility(View.VISIBLE);
                        btnPrev.setVisibility(View.VISIBLE);
//                    } else if(current == layouts.length - 1){
//                        viewPager.setCurrentItem(current);
//                        btnNext.setVisibility(View.GONE);
                    }else if(current == layouts.length-1){
                        viewPager.setCurrentItem(current);
                        btnNext.setText(getString(R.string.exit));

                    }else{

                        launchHomeScreen();
                    }

                }
            }); */
            return v;
        }

        private void addBottomDots(int currentPage) {
            dots = new TextView[layouts.length];

            int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
            int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

            dotsLayout.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(getActivity());
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(colorsInactive[currentPage]);
                dotsLayout.addView(dots[i]);
            }

            if (dots.length > 0)
                dots[currentPage].setTextColor(colorsActive[currentPage]);
        }

        private int getItem(int i) {
            return viewPager.getCurrentItem() + i;
        }

        private void launchHomeScreen() {
            // Create new fragment and transaction
           /* ListingsTabActivity.PlaceholderFragment frg = ListingsTabActivity.PlaceholderFragment.newInstance(0);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            // Add animation in transition
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.container, frg, "LISTINGS_TAG");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();*/
            HomeActivity.listingsTab.performClick();
        }
        //  viewpager change listener
        ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                // changing the next button text 'NEXT' / 'EXIT'
                if (position == layouts.length - 1) {
                    // last page. make button text to EXIT
                    btnNext.setText(getString(R.string.exit));
                  //  btnNext.setVisibility(View.GONE);
                    btnPrev.setVisibility(View.VISIBLE);
                } else if (position == 0){
                    btnPrev.setVisibility(View.GONE);
                } else {
                    // still pages are left
                    btnNext.setText(getString(R.string.next));
                    btnNext.setVisibility(View.VISIBLE);
                    btnPrev.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };


        /**
         * View pager adapter
         */
        public class MyViewPagerAdapter extends PagerAdapter {
            private LayoutInflater layoutInflater;

            public MyViewPagerAdapter() {
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(layouts[position], container, false);
                container.addView(view);

                return view;
            }

            @Override
            public int getCount() {
                return layouts.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                mCurrentView = (View)object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View view = (View) object;
                container.removeView(view);
            }

        }
    }


}