package edu.sdsu.cs.ramya.rssreader;

import com.parse.ParseFile;


public class SubscribedFeedDetails {
    String feedTitle;
    int    feedId;
    ParseFile feedImage;
    String    feedUrl;
    boolean    CustomFeed;

    public SubscribedFeedDetails(String feedTitle, int feedId, ParseFile feedImage, String feedUrl,
                                 boolean customFeed)
    {
        this.feedTitle = feedTitle;
        this.feedId = feedId;
        this.feedImage = feedImage;
        this.feedUrl = feedUrl;
        this.CustomFeed = customFeed;
    }


    public boolean isCustomFeed()
    {
        return CustomFeed;
    }

    public void setCustomFeed(boolean customFeed)
    {
        CustomFeed = customFeed;
    }

    public String getFeedUrl()
    {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl)
    {
        this.feedUrl = feedUrl;
    }

    public ParseFile getFeedImage()
    {
        return feedImage;
    }

    public void setFeedImage(ParseFile feedImage)
    {
        this.feedImage = feedImage;
    }



    public int getFeedId()
    {
        return feedId;
    }

    public void setFeedId(int feedId)
    {
        this.feedId = feedId;
    }


    public String getFeedTitle()
    {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle)
    {
        this.feedTitle = feedTitle;
    }



}
