package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class RSSItemsListActivity extends ActionBarActivity implements
        SearchView.OnQueryTextListener,SwipeRefreshLayout.OnRefreshListener,
        RSSItemsListFragment.OnSwipeRefreshStatus
{
    private String rssItemUrl;
    private ProgressBar progressBar;
    private TextView loadingLabel;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssitems_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeUI();
    }

    private void initializeUI()
    {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingLabel = (TextView) findViewById(R.id.loadingLabel);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        Bundle intentExtras = getIntent().getExtras();
        rssItemUrl = intentExtras.getString("rssItemUrl");
        String feedTitle = intentExtras.getString("feedTitle");
        this.setTitle(feedTitle);
        progressBar.setVisibility(View.VISIBLE);
        loadingLabel.setVisibility(View.VISIBLE);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        fetchFeedItems(rssItemUrl);
    }

    private void fetchFeedItems(String feedURLString)
    {
        URL feedUrl = null;
        try {

            feedUrl = new URL(feedURLString);
            URI uri = new URI(feedUrl.getProtocol(), feedUrl.getUserInfo(),
                    feedUrl.getHost(), feedUrl.getPort(), feedUrl.getPath(),
                    feedUrl.getQuery(), feedUrl.getRef());
            feedUrl = uri.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
        RSSPullParser.getRSSFeedsFromUrl(feedUrl, RSSItemsListActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_rssitems, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView menuSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        menuSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        if(id == android.R.id.home)
        {
            finish();
            return true;
        }
        if (id == R.id.action_feed_settings)
        {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RSSItemsListFragment listFragment = (RSSItemsListFragment) fragmentManager.
                                                                     findFragmentByTag("list");
        listFragment.getFilter(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RSSItemsListFragment listFragment = (RSSItemsListFragment) fragmentManager.
                                                                     findFragmentByTag("list");
        listFragment.getFilter(s);
        return true;
    }

    @Override
    public void onRefresh()
    {
        fetchFeedItems(rssItemUrl);
    }

    public void parsingDone()
    {
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        RSSItemsListFragment rssItemsListFragment = (RSSItemsListFragment)manager.
                                                       findFragmentByTag("list");
        if (rssItemsListFragment == null)
        {
            rssItemsListFragment = new RSSItemsListFragment();
            transaction.add(R.id.listFragment_layout, rssItemsListFragment, "list");
            transaction.commitAllowingStateLoss();
        }
        else
        {
            rssItemsListFragment.refreshData();
        }
        progressBar.setVisibility(View.GONE);
        if (RSSItemsSingleton.get(RSSItemsListActivity.this).getRSSItems().size() == 0)
        {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)loadingLabel.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            loadingLabel.setLayoutParams(layoutParams);
            if(!isOnline())
            {
              loadingLabel.setText(R.string.no_offline_articles_message);

            }
            else
            {
                loadingLabel.setText(R.string.no_articles);
            }
        }
        else
        {
            loadingLabel.setVisibility(View.GONE);
        }
        stopRefreshing();
    }

    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void stopRefreshing()
    {
        if (swipeLayout.isRefreshing())
        {
            swipeLayout.setRefreshing(false);
        }
    }

    public void changeSwipeLayoutStatus(boolean enable)
    {
        swipeLayout.setEnabled(enable);
    }
}


