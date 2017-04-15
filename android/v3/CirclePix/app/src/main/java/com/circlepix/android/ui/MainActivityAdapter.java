package com.circlepix.android.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.circlepix.android.CustomerSupportActivityTab;
import com.circlepix.android.ListingsTabActivity;
import com.circlepix.android.PresentationsTabActivity;

/**
 * Created by keuahnlumanog1 on 4/30/16.
 */
public class MainActivityAdapter extends FragmentStatePagerAdapter {

    public MainActivityAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //   return DesignDemoFragment.newInstance(position);

        switch (position) {
            //for 4 Tabs
          /*  case 0: // Fragment # 0 - This will show FirstFragment
                return ListingsTabActivity.PlaceholderFragment.newInstance(0);
            case 1:
                return SocialCalendarTabActivity.PlaceholderFragment.newInstance(1);
            case 2:
                return LeadsTabActivity.PlaceholderFragment.newInstance(2);
            case 3:
                return PresentationsTabActivity.PlaceholderFragment.newInstance(3);
            default:
                return null;*/

            //for 3 Tabs
            case 0: // Fragment # 0 - This will show FirstFragment
                return ListingsTabActivity.PlaceholderFragment.newInstance(0);
            case 1:
                return PresentationsTabActivity.PlaceholderFragment.newInstance(1);
            case 2:
                return CustomerSupportActivityTab.PlaceholderFragment.newInstance(2);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;  //number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //  return "Tab " + position;
        return null;
    }

}