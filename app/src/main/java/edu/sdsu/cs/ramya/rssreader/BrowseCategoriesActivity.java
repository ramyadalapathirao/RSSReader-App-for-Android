package edu.sdsu.cs.ramya.rssreader;


import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import edu.sdsu.cs.ramya.rssreader.adapter.TabsPagerAdapter;
import edu.sdsu.cs.ramya.rssreader.tabs.SlidingTabLayout;


public class BrowseCategoriesActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_categories);
        setTitle(getResources().getString(R.string.categories));
        ViewPager tabsPager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        tabsPager.setAdapter(tabsAdapter);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(tabsPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_browse_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if(itemID == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

}
