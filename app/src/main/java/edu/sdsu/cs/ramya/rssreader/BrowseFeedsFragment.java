package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.sdsu.cs.ramya.rssreader.adapter.FeedsListAdapter;

public class BrowseFeedsFragment extends Fragment
{
    private ListView feedsListView;
    private ArrayList<ParseObject> feedsArray;
    private ProgressBar spinner;
    private TextView loadingLabel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                                      @Nullable Bundle savedInstanceState)
    {
        View feedsLayout = inflater.inflate(R.layout.fragment_browse_categories,container,false);
        feedsListView = (ListView)feedsLayout.findViewById(R.id.list);
        feedsListView.setDividerHeight(2);
        feedsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        spinner = (ProgressBar)feedsLayout.findViewById(R.id.progressBar);
        loadingLabel = (TextView)feedsLayout.findViewById(R.id.loadingLabel);
        return feedsLayout;
    }

    public static BrowseFeedsFragment newInstance(int categoryId,String categoryTitle)
    {
        BrowseFeedsFragment fragment = new BrowseFeedsFragment();
        Bundle args = new Bundle();
        args.putInt("categoryId",categoryId);
        args.putString("categoryTitle",categoryTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fetchFeedsList();
    }

    public void setCheckedItems(ArrayList<Integer> subscribedFeed)
    {
        for(int i=0;i<feedsListView.getAdapter().getCount();i++)
        {
            if(subscribedFeed.contains(feedsArray.get(i).getInt("feed_id")))
            {
                feedsListView.setItemChecked(i,true);
            }
            else
            {
                feedsListView.setItemChecked(i,false);

            }
        }
        spinner.setVisibility(View.GONE);
        loadingLabel.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_done:
                subscribeToSelectedFeeds();
                return true;
            default:
                break;
        }
        return false;
    }

    private void subscribeToSelectedFeeds()
    {
        if(!isOnline()) {
            if (getActivity() != null)
            {
                AlertDialogFragment alertDialog = AlertDialogFragment.
                        newInstance(getString(R.string.network_error),
                                      getString(R.string.add_feed_offline_error));
                alertDialog.show(getFragmentManager(), "alert");
            }
        }
        else {
            SparseBooleanArray checkedFeeds = feedsListView.getCheckedItemPositions();
            ArrayList<Integer> subscribedFeedIDs = new ArrayList<>();
            ArrayList<Boolean> isSubscribed = new ArrayList<>();
            for (int i = 0; i < feedsListView.getAdapter().getCount(); i++) {
                subscribedFeedIDs.add(feedsArray.get(i).getInt("feed_id"));
                if (checkedFeeds.get(i)) {
                    isSubscribed.add(true);
                } else {
                    isSubscribed.add(false);
                }
            }
            if (getActivity() != null) {
                SubscribedFeedsDBTask dbTask = new SubscribedFeedsDBTask("feedsInsert",
                        getActivity(), subscribedFeedIDs, isSubscribed);
                dbTask.execute();
            }
        }

    }

    private void fetchFeedsList()
    {
        ParseQuery<ParseObject> getFeedsQuery = ParseQuery.getQuery("feeds");
        getFeedsQuery.selectKeys(Arrays.asList("feed_id", "feed_title","feed_image"));
        getFeedsQuery.whereEqualTo("category_id", getArguments().getInt("categoryId"));
        getFeedsQuery.orderByAscending("feed_id");
        getFeedsQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        if(getFeedsQuery.hasCachedResult()  || isOnline())
        {
            spinner.setVisibility(View.VISIBLE);
            loadingLabel.setVisibility(View.VISIBLE);
        }
        getFeedsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null)
                {
                    feedsArray =new ArrayList<>();
                    feedsArray.addAll(parseObjects);
                    if(getActivity() != null)
                    {
                        FeedsListAdapter listAdapter = new FeedsListAdapter(getActivity(),
                                R.layout.fragment_browse_feeds, feedsArray);
                        feedsListView.setAdapter(listAdapter);
                    }
                    SubscribedFeedsDBTask dbTask = new SubscribedFeedsDBTask("feedsQuery",
                                                                           getActivity(),null,null);
                    dbTask.execute();
                }
                else
                {
                    if(e.getMessage().equals("results not cached") && !isOnline())
                    {
                        RelativeLayout.LayoutParams layoutParams =
                                (RelativeLayout.LayoutParams)loadingLabel.getLayoutParams();
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        loadingLabel.setLayoutParams(layoutParams);
                        loadingLabel.setVisibility(View.VISIBLE);
                        loadingLabel.setTextAppearance(getActivity(),
                                android.R.style.TextAppearance_Large);
                        loadingLabel.setText(getString(R.string.no_offline_feeds_message));
                    }
                }
            }
        });
    }

    public void goToMain()
    {
        Intent goBack = new Intent(getActivity(),UserSubscriptionsActivity.class);
        goBack.putExtra("title",getArguments().getString("categoryTitle"));
        goBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBack);
    }

    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
