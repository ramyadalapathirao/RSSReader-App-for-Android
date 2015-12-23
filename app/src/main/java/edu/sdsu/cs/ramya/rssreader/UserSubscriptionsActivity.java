package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.sdsu.cs.ramya.rssreader.adapter.CardItemAdapter;


public class UserSubscriptionsActivity extends ActionBarActivity
{

    ListView cardsListView;
    List<String> categoryTitlesList;
    HashMap<String, List<SubscribedFeedDetails>> subscribedFeedDetailsMap;
    private ArrayList<SubscribedFeedDetails> customSubscribedFeedList;
    CardItemAdapter cardAdapter;
    ArrayList<Integer> subscribedFeeds;
    TextView noSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_subscriptions);
        setTitle(getResources().getString(R.string.title_activity_subscriptions));
        cardsListView = (ListView)findViewById(R.id.cardListView);
        noSubscriptions = (TextView)findViewById(R.id.noSubscriptions);
        if(!isOnline())
        {
            Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SubscribedFeedsDBTask getSubscribedFeedsTask = new SubscribedFeedsDBTask(
                "subscriptionsQuery",this,null,null);
        getSubscribedFeedsTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent showCategories = new Intent(this,BrowseCategoriesActivity.class);
            startActivity(showCategories);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void prepareListData()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("feeds");
        query.selectKeys(Arrays.asList("feed_id", "feed_title", "feed_url", "feed_image",
                "category_id", "category_pointer.category_name"));
        query.orderByAscending("category_id");
        query.whereContainedIn("feed_id",subscribedFeeds);
        query.include("category_pointer");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e)
            {
                if (e == null)
                {
                    int currentCategoryIndex = 0;
                    int previousCategoryIndex = -1;
                    List<SubscribedFeedDetails> subscribedFeedsDetailsList = new ArrayList<>();
                    int categoryIndex = -1;
                    categoryTitlesList = new ArrayList<>();
                    subscribedFeedDetailsMap = new HashMap<>();
                    for(int i = 0;i<parseObjects.size();i++)
                    {
                        currentCategoryIndex = (int) parseObjects.get(i).get("category_id");
                        if (currentCategoryIndex != previousCategoryIndex) {
                            categoryIndex++;
                            subscribedFeedsDetailsList = new ArrayList<>();
                            categoryTitlesList.add(parseObjects.get(i).
                                    getMap("category_pointer").get("category_name").toString());
                        }
                        String feedTitle = parseObjects.get(i).get("feed_title").toString();
                        int feedId = (int) parseObjects.get(i).get("feed_id");
                        ParseFile feedImage = (ParseFile) parseObjects.get(i).get("feed_image");
                        String feedUrl = parseObjects.get(i).get("feed_url").toString();
                        subscribedFeedsDetailsList.add(new SubscribedFeedDetails(feedTitle,
                                feedId, feedImage, feedUrl, false));
                        previousCategoryIndex = currentCategoryIndex;
                        subscribedFeedDetailsMap.put(categoryTitlesList.get(categoryIndex),
                                subscribedFeedsDetailsList);

                    }
                    addCustomFeedToSubscriptionsList(categoryIndex);
                    if(cardsListView.getAdapter() == null) {
                        cardAdapter = new CardItemAdapter(UserSubscriptionsActivity.this,
                                categoryTitlesList, subscribedFeedDetailsMap);
                        cardsListView.setAdapter(cardAdapter);
                    }
                    else
                    {
                        ((CardItemAdapter)cardsListView.getAdapter()).refetchData(categoryTitlesList,
                                subscribedFeedDetailsMap);
                    }
                    setCardLayoutHeight();
                    cardAdapter.notifyDataSetChanged();
                    displaySubscriptionsStatus();
                    scrollToSpecificListPosition();
                }
                else
                {
                    if(e.getMessage().equals("results not cached") && !isOnline())
                    {
                        noSubscriptions.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

    }

    private void addCustomFeedToSubscriptionsList(int categoryIndex) {
        if (customSubscribedFeedList != null)
        {
            if(customSubscribedFeedList.size()>0)
            {
                categoryIndex++;
                List<SubscribedFeedDetails> subscribedFeedsDetailsList = new ArrayList<>();
                categoryTitlesList.add(getResources().getString(R.string.custom_category_title));
                for (int i = 0; i < customSubscribedFeedList.size(); i++) {
                    subscribedFeedsDetailsList.add(customSubscribedFeedList.get(i));
                }
                subscribedFeedDetailsMap.put(categoryTitlesList.get(categoryIndex),
                        subscribedFeedsDetailsList);
            }
        }
    }

    private void scrollToSpecificListPosition() {
        if(getIntent().getStringExtra("title") != null)
        {
            int index = 0;
            String headerTitle = getIntent().getStringExtra("title");
            for(index=0;index < categoryTitlesList.size();index++)
            {
                if(categoryTitlesList.get(index).equals(headerTitle))
                {
                    break;
                }
            }
            cardsListView.setSelection(index);
            getIntent().removeExtra("title");
        }
    }

    private void setCardLayoutHeight() {
        if (cardAdapter != null)
        {

            int numberOfItems = cardAdapter.getCount();
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = cardAdapter.getView(itemPos, null, cardsListView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            // Get total height of all item dividers.
            int totalDividersHeight = cardsListView.getDividerHeight() *
                    (numberOfItems - 1);
            // Set list height.
            ViewGroup.LayoutParams params = cardsListView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            cardsListView.setLayoutParams(params);
            cardsListView.requestLayout();
        }
    }

    public void displaySubscriptionsStatus()
    {
        if(cardAdapter.getCount() == 0)
        {
            noSubscriptions.setVisibility(View.VISIBLE);
        }
        else
        {
            noSubscriptions.setVisibility(View.GONE);
        }
    }

    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void retrieveSubscribedFeeds(ArrayList<Integer> subsFeeds)
    {
        this.subscribedFeeds =subsFeeds;
        CustomSubscriptionDBTask getCustomSubscriptions=new CustomSubscriptionDBTask
                                                          ("customQuery",this,null,null,null);
        getCustomSubscriptions.execute();
    }

    public void retrieveCustomSubscribedFeeds(ArrayList<SubscribedFeedDetails> customSubscriptions)
    {
        this.customSubscribedFeedList=customSubscriptions;
        prepareListData();
    }

}
