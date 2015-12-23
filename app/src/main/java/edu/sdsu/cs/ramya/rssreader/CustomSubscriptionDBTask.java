package edu.sdsu.cs.ramya.rssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import java.util.ArrayList;


public class CustomSubscriptionDBTask extends AsyncTask<Void, Void, Void>
{

    private String requestType;
    private Context context;
    private String customFeedTitle;
    private String customFeedRssLink;
    private ArrayList<SubscribedFeedDetails> customFeedList;
    private SearchFeedFragment  searchFragment;

    public CustomSubscriptionDBTask(String dbRequestType, Context context,
                                   String title, String link, SearchFeedFragment searchfeedFragment)
    {
        this.requestType = dbRequestType;
        this.context = context;
        this.customFeedTitle = title;
        this.customFeedRssLink = link;
        this.searchFragment =searchfeedFragment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        if(requestType.equals("customInsert"))
        {
            ContentValues newRecord = new ContentValues();
            newRecord.put("feedTitle", customFeedTitle);
            newRecord.put("feedUrl",customFeedRssLink);
            database.replace("CustomSubscription", null, newRecord);
        }

        if(requestType.equals("customQuery") )
        {
            customFeedList= new ArrayList<>();
            Cursor result = database.rawQuery("select feedTitle,feedUrl from CustomSubscription",
                                                                                           null);
            try
            {
                result.moveToFirst();
                while (!result.isAfterLast())
                {
                    String feedTitle =result.getString(0);
                    String feedUrl = result.getString(1);
                    SubscribedFeedDetails customFeed = new SubscribedFeedDetails(feedTitle,100,null,
                                                                                      feedUrl,true);
                    result.moveToNext();
                    customFeedList.add(customFeed);
                }
            }
            finally
            {
                result.close();
            }
        }

        if(requestType.equals("customDelete"))
        {
            String[] whereArgs = new String[] { String.valueOf(customFeedTitle) };
            database.delete("CustomSubscription", "feedTitle" + "=?", whereArgs);
        }
        return null;
    }

    protected void onPostExecute(Void result)
    {

        if(requestType.equals("customQuery") && context != null)
        {
            UserSubscriptionsActivity subsActivity = (UserSubscriptionsActivity)context;
            subsActivity.retrieveCustomSubscribedFeeds(customFeedList);

        }
        if(requestType.equals("customInsert") && context != null)
        {

          searchFragment.customFeedInserted();
        }

    }
}
