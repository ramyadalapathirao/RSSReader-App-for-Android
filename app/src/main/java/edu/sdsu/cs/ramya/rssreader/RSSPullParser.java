package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Xml;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class RSSPullParser
{
    public static final String KEY_ITEM="item";
    public static final String KEY_TITLE="title";
    public static final String KEY_LINK="link";
    public static final String KEY_DATE="pubDate";
    public static final String KEY_ENTRY="entry";
    private static final String MAX_ITEMS_COUNT_KEY = "feedCount";
    public static final String PREFS = "preferencesKey";
    private static int feedCount = 0;
    private static Context context;
    private static boolean shouldFetchAllItems;
    public static void getRSSFeedsFromUrl(URL url,Context activityContext)
    {
        context = activityContext;
        shouldFetchAllItems = false;
        RSSItemsSingleton.get(activityContext).clearRSSItemArray();
        SharedPreferences prefs = activityContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int listPosition = prefs.getInt(MAX_ITEMS_COUNT_KEY,-1);
        if(listPosition == 5)
        {
            shouldFetchAllItems = true;
        }
        else if(listPosition == -1)
        {
            shouldFetchAllItems = true;
        }
        else
        {
            String[] counts = activityContext.getResources().getStringArray(R.array.feeds_count);
            feedCount = Integer.parseInt(counts[listPosition]);
            shouldFetchAllItems = false;
        }

        if(url.toString().equals(""))
        {
            RSSItemsListActivity itemsActivity = (RSSItemsListActivity)context;
            itemsActivity.parsingDone();

        }
        RequestQueue requestQueue=VolleySingleton.getsInstance().getRequestQueue();
        Response.Listener<String> success = new Response.Listener<String>()
        {
            public void onResponse(String response)
            {
                parseXML(response);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener()
        {
            public void onErrorResponse(VolleyError error)
            {
                if(context != null)
                {
                    RSSItemsListActivity itemsActivity = (RSSItemsListActivity)context;
                    itemsActivity.parsingDone();
                }
            }
        };
        StringRequest request=new StringRequest(Request.Method.GET,url.toString(),success,failure);
        Cache.Entry entry=requestQueue.getCache().get(url.toString());
        if(entry != null )
        {
            //Data coming from cache
            String detailsObject = null;
            try
            {
                detailsObject = new String(entry.data,"UTF8");

            }
            catch (UnsupportedEncodingException e1)
            {
                    e1.printStackTrace();
            }
            parseXML(detailsObject);
        }
        else
        {
            if(isOnline())
            {
                requestQueue.add(request);
            }
            else
            {
                RSSItemsListActivity itemsActivity = (RSSItemsListActivity)context;
                itemsActivity.parsingDone();

            }
        }

    }
    public static void parseXML(String response)
    {
        RSSItem currentItem=null;
        String currentText="";
        boolean endOfDocument = false;
        XmlPullParserFactory factory= null;
        XmlPullParser parser= null;
        try
        {
            factory = XmlPullParserFactory.newInstance();
            if (factory != null)
            {
                parser = factory.newPullParser();
            }
            if (parser != null)
            {
                parser.setFeature(Xml.FEATURE_RELAXED,true);
            }
            BufferedReader reader=new BufferedReader(new StringReader(response));
            if (parser != null)
            {
                parser.setInput(reader);
                parser.defineEntityReplacementText("^.*<","<");
            }
        }
        catch(XmlPullParserException ex)
        {
            ex.getMessage();
        }
        int eventType= 0;
        try
        {
            eventType = parser != null ? parser.getEventType() : 0;
        }
        catch (XmlPullParserException e1)
        {
            e1.printStackTrace();
        }
        while(eventType!=XmlPullParser.END_DOCUMENT) {
            String tagName = null;
            if (parser != null) {
                tagName = parser.getName();
            }
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase(KEY_ITEM)) {
                        currentItem = new RSSItem();
                        currentItem.setAtom(false);
                    }
                    if(tagName.equalsIgnoreCase(KEY_ENTRY))
                    {
                        currentItem =new RSSItem();
                        currentItem.setAtom(true);
                    }
                    if(tagName.equalsIgnoreCase(KEY_LINK) && currentItem!=null &&
                            currentItem.isAtom())
                    {
                        String link = parser.getAttributeValue("", "href");
                        currentItem.setLink(parser.getAttributeValue("","href"));
                    }

                    break;
                case XmlPullParser.TEXT:
                    currentText = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase(KEY_ITEM))
                    {

                        if((RSSItemsSingleton.get(context).getRSSItemsCount() < feedCount) ||
                                shouldFetchAllItems)
                        {
                            RSSItemsSingleton.get(context).setRSSItems(currentItem);
                        }
                        else
                        {
                            endOfDocument = true;
                            break;
                        }
                    }
                    else if(tagName.equalsIgnoreCase(KEY_ENTRY))
                    {

                        if((RSSItemsSingleton.get(context).getRSSItemsCount() < feedCount)
                                || shouldFetchAllItems)
                        {
                            RSSItemsSingleton.get(context).setRSSItems(currentItem);

                        }
                        else
                        {
                            endOfDocument = true;
                            break;
                        }
                    }
                    else if (tagName.equalsIgnoreCase(KEY_TITLE))
                    {
                        if (currentItem != null) {
                            currentItem.setTitle(currentText);
                        }
                    }
                    else if (currentItem != null && (!currentItem.isAtom()) &&
                            tagName.equalsIgnoreCase(KEY_LINK))
                    {
                        currentItem.setLink(currentText);

                    }
                    else if (tagName.equalsIgnoreCase(KEY_DATE))
                    {
                        if (currentItem != null) {
                            currentItem.setPubDate(currentText);
                        }
                    }
                    break;
                default:
                    break;

            }
            if(endOfDocument)
            {
                break;
            }
            try {
                eventType = parser.next();
            } catch (XmlPullParserException | IOException e1) {
                e1.printStackTrace();
            }
        }

      if(context != null)
      {
          RSSItemsListActivity itemsActivity = (RSSItemsListActivity)context;
          itemsActivity.parsingDone();
      }
    }


    public static boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}
