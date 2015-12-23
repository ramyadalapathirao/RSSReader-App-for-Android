package edu.sdsu.cs.ramya.rssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import java.util.ArrayList;


public class SubscribedFeedsDBTask extends AsyncTask<Void, Void, Void>
{
    private String requestType;
    private Context context;
    private ArrayList<Integer> feedList;
    private ArrayList<Boolean> feedSubscribed;
    private ArrayList<Integer> subscribedFeeds;
    public SubscribedFeedsDBTask(String dbRequestType, Context context,
                                 ArrayList<Integer> fList, ArrayList<Boolean> fSubscribed)
    {
        this.requestType = dbRequestType;
        this.context = context;
        this.feedList = fList;
        this.feedSubscribed = fSubscribed;
    }

    @Override
    protected Void doInBackground(Void... strings)
    {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        subscribedFeeds = new ArrayList<>();
        if(requestType.equals("feedsQuery") || requestType.equals("subscriptionsQuery"))
        {
            Cursor result = database.rawQuery("select feedId from FeedList where isSubscribed=1",
                                               null);
            try
            {
                result.moveToFirst();
                while (!result.isAfterLast())
                {
                    subscribedFeeds.add(result.getInt(0));
                    result.moveToNext();
                }
            }
            finally
            {
                result.close();
            }
        }
        if(requestType.equals("feedsInsert")||
                requestType.equals("subscriptionsInsert"))
        {
            for(int i=0;i< feedList.size();i++)
            {
                ContentValues newRecord = new ContentValues();
                newRecord.put("feedId", feedList.get(i));
                newRecord.put("isSubscribed",feedSubscribed.get(i));
                database.replace("FeedList", null, newRecord);
            }
        }
        return null;
    }

    protected void onPostExecute(Void result)
    {
        if(requestType.equals("feedsQuery") && context != null)
        {
            BrowseFeedsFragment feedsFragment = ((BrowseFeedsActivity)context).feedsFragment;
            feedsFragment.setCheckedItems(subscribedFeeds);
        }
        if(requestType.equals("subscriptionsQuery") && context!= null)
        {
            UserSubscriptionsActivity subsActivity = (UserSubscriptionsActivity)context;
            subsActivity.retrieveSubscribedFeeds(subscribedFeeds);

        }
        if(requestType.equals("feedsInsert") && context!=null)
        {
            BrowseFeedsFragment feedsFragment = ((BrowseFeedsActivity)context).feedsFragment;
            feedsFragment.goToMain();
        }

    }
}
