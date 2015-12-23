package edu.sdsu.cs.ramya.rssreader;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Rss.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper sInstance;

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context)
    {
        if (sInstance == null)
        {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDb)
    {
        String createSubscriptionsTableSql = "CREATE TABLE IF NOT EXISTS FeedList " +
                "(feedId INTEGER PRIMARY KEY,isSubscribed BOOL);";
        sqlDb.execSQL(createSubscriptionsTableSql);
        String createCustomSubscriptionsTableSql = "CREATE TABLE IF NOT EXISTS CustomSubscription "
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,feedTitle TEXT,feedUrl TEXT UNIQUE);";
        sqlDb.execSQL(createCustomSubscriptionsTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }
}
