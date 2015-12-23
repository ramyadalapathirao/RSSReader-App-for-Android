package edu.sdsu.cs.ramya.rssreader;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class BrowseFeedsActivity extends ActionBarActivity
{

    BrowseFeedsFragment feedsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_feeds);
        Intent receivedIntent = getIntent();
        int categoryID = receivedIntent.getIntExtra("categoryId",0);
        String categoryTitle = receivedIntent.getStringExtra("categoryTitle");
        setTitle(categoryTitle);
        FragmentManager listManager = getSupportFragmentManager();
        if(savedInstanceState == null) {

            FragmentTransaction transaction = listManager.beginTransaction();
            feedsFragment = BrowseFeedsFragment.newInstance(categoryID,categoryTitle);
            transaction.add(R.id.feeds_container, feedsFragment,"listFragment");
            transaction.commit();
        }
        else
        {
            feedsFragment = (BrowseFeedsFragment)listManager.findFragmentByTag("listFragment");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_feeds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        if (id == R.id.action_done) {
            return false;
        }
        if(id == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
