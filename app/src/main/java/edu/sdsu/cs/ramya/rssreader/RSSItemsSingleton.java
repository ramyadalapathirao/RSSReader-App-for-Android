package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;

import java.util.ArrayList;

public class RSSItemsSingleton {
    private ArrayList<RSSItem> rssItemsArray;
    private Context applicationContext;
    private static RSSItemsSingleton sRSSItemLab;

    private RSSItemsSingleton(Context appContext)
    {
        applicationContext= appContext;
        rssItemsArray = new ArrayList<>();
    }

    public static RSSItemsSingleton get(Context c)
    {
        if(sRSSItemLab == null)
        {
            sRSSItemLab = new RSSItemsSingleton(c.getApplicationContext());

        }
        return sRSSItemLab;
    }

    public ArrayList<RSSItem> getRSSItems()
    {
        return rssItemsArray;
    }

    public void setRSSItems(RSSItem item)
    {
        rssItemsArray.add(item);
    }

    public int getRSSItemsCount()
    {
        return rssItemsArray.size();
    }

    public void clearRSSItemArray()
    {
        rssItemsArray.clear();
    }


}
