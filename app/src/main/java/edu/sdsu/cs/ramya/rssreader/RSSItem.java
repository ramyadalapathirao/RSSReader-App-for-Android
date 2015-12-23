package edu.sdsu.cs.ramya.rssreader;

import android.util.Log;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RSSItem {
    private boolean isAtom;
    private String title;
    private String link;
    private String pubDate;


    public boolean isAtom() {
        return isAtom;
    }

    public void setAtom(boolean isAtom) {
        this.isAtom = isAtom;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
    @Override
    public String toString() {
        return getTitle();
    }

    public String getSubTitle() throws ParseException
    {
        String pubDate = this.getPubDate();
        String dateSubTitle = "";
        if(pubDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                                                                                         Locale.US);
            Date date;
            date = dateFormat.parse(pubDate);
            String timeOfDay = new SimpleDateFormat("HH:mm").format(date);
            Timestamp pubDateTimeStamp = new Timestamp(date.getTime());
            Timestamp currentTimeStamp = new Timestamp((new Date()).getTime());
            long secsDifference = currentTimeStamp.getTime() / 1000 -
                                  pubDateTimeStamp.getTime() / 1000;
            int minuteDifference = (int) (secsDifference / 60);
            int hourDifference = (int) (secsDifference / 3600);
            int dayDifference = daysDifference(date, new Date()) - 1;
            if (dayDifference > 0) {
                if (dayDifference == 1) {
                    dateSubTitle = "Posted " + dayDifference + " day ago";
                }
                else if(dayDifference >= 30)
                {
                    int months = (int)dayDifference/30;
                    dateSubTitle = "Posted " + months + " day ago";
                }
                else {
                    dateSubTitle = "Posted " + dayDifference + " days ago";
                }
            } else if (hourDifference > 0) {
                if (hourDifference == 1) {
                    dateSubTitle = "Posted " + hourDifference + " hour ago";
                } else {
                    dateSubTitle = "Posted " + hourDifference + " hours ago";
                }

            } else if (minuteDifference > 0) {
                if (minuteDifference == 1) {
                    dateSubTitle = "Posted " + minuteDifference + " minute ago";
                } else {
                    dateSubTitle = "Posted " + minuteDifference + " minutes ago";
                }

            } else if (secsDifference > 0) {
                if (secsDifference == 1) {
                    dateSubTitle = "few second ago";
                } else {
                    dateSubTitle = "few seconds ago";
                }

            }
        }
            return dateSubTitle;
    }

    public static int daysDifference(Date startDate,Date endDate)
    {
        int days = 0;
        while(startDate.before(endDate))
        {
            startDate.setTime(startDate.getTime()+86400000);
            days++;
        }
        return days;
    }

    public int getPostedTimeInDays(RSSItem item)
    {
        String pubdate = item.getPubDate();
        if(pubdate != null)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

}
