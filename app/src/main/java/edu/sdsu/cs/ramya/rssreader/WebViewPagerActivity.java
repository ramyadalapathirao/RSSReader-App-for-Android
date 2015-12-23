package edu.sdsu.cs.ramya.rssreader;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;


public class WebViewPagerActivity extends ActionBarActivity
{
    ViewPager webViewPager;
    WebPagerAdapter pagerAdapter;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        if(RSSItemsSingleton.get(this).getRSSItemsCount() == 0)
        {
            Intent goBack = new Intent(this,UserSubscriptionsActivity.class);
            goBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goBack);
            return;
        }
        setContentView(R.layout.activity_webview_pager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS,Window.PROGRESS_VISIBILITY_ON);
        webViewPager = (ViewPager) findViewById(R.id.webContainer);
        webViewPager.setOffscreenPageLimit(2);
        int position=getIntent().getIntExtra("currentItemPosition",-1);
        title = getIntent().getStringExtra("title");
        FragmentManager manager = getSupportFragmentManager();
        pagerAdapter = new WebPagerAdapter(manager);
        webViewPager.setAdapter(pagerAdapter);
        if(position == -1)
        {
            webViewPager.setCurrentItem(0);
        }
        else
        {
            webViewPager.setCurrentItem(position);
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        int currentFragmentIndex = webViewPager.getCurrentItem();
        String currentUrl = RSSItemsSingleton.get(WebViewPagerActivity.this).getRSSItems()
                .get(currentFragmentIndex).getLink();
        if (!currentUrl.startsWith("http://") && !currentUrl.startsWith("https://")) {
            currentUrl = "http://" + currentUrl;
        }
        if (id == R.id.action_open_in_browser)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl));
            startActivity(browserIntent);
            return true;
        }
        if(id == android.R.id.home)
        {
            finish();
        }
        if(id == R.id.share_facebook)
        {
            shareStory(currentUrl);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareStory(String currentUrl)
    {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentUrl);
            startActivity(Intent.createChooser(shareIntent,"Share URL"));
        }
        catch(Exception e)
        {
            Log.d("exception",e.getMessage());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    int index = webViewPager.getCurrentItem();
                    WebViewPagerFragment currentFragment =pagerAdapter.getRegisteredFragment(index);
                    if(currentFragment != null) {
                        if (currentFragment.canGoBack())
                        {
                            currentFragment.goBack();
                        }
                        else
                        {
                            finish();
                        }
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public class WebPagerAdapter extends FragmentStatePagerAdapter
    {
        SparseArray<WebViewPagerFragment> registeredFragments = new SparseArray<>();
        public WebPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return WebViewPagerFragment.newInstance(
                    RSSItemsSingleton.get(WebViewPagerActivity.this).getRSSItems().
                            get(position).getLink(), title);
        }

        @Override
        public int getCount()
        {
            return RSSItemsSingleton.get(WebViewPagerActivity.this).getRSSItems().size();
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            WebViewPagerFragment fragment = (WebViewPagerFragment)super.instantiateItem(container,
                                                                                        position);
            registeredFragments.put(position,fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }
        public WebViewPagerFragment getRegisteredFragment(int position)
        {
            return registeredFragments.get(position);
        }
    }
}
