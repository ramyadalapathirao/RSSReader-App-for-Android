package edu.sdsu.cs.ramya.rssreader;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleySingleton {
    private static VolleySingleton sInstance=null;
    private RequestQueue requestQueue;
    private VolleySingleton()
    {
        requestQueue = Volley.newRequestQueue
                (RSSReaderApplication.getAppContext());

    }
    public static VolleySingleton getsInstance()
    {
        if(sInstance == null)
        {
            sInstance = new VolleySingleton();
        }

        return sInstance;
    }
    public RequestQueue getRequestQueue()
    {

        return requestQueue;
    }
}
