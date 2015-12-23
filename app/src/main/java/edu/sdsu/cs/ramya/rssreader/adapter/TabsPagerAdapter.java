package edu.sdsu.cs.ramya.rssreader.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.sdsu.cs.ramya.rssreader.BrowseCategoriesFragment;
import edu.sdsu.cs.ramya.rssreader.SearchFeedFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private final int TAB_COUNT = 2;
    private String tabTitles[] = new String[]{"Browse","Search"};
    public TabsPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new BrowseCategoriesFragment();
            case 1:
                return new SearchFeedFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
