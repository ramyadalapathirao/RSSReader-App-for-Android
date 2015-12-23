package edu.sdsu.cs.ramya.rssreader;

import android.app.Application;
import android.content.Context;
import com.parse.Parse;

public class gRSSReaderApplication extends Application
{
    private static RSSReaderApplication sInstance;

    public static RSSReaderApplication getInstance()
    {
        return sInstance;
    }

    public static Context getAppContext()
    {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sInstance = this;
        Parse.initialize(this, "jsbWute97AHnBnOSu5H8wjbFp2fFTEljSRR2PkX1",
                "t2XuFVA9wLfBnM389chI4I1DUXesS0Bni7nl4nGG");

    }




}
