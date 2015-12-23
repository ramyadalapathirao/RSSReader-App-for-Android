package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import edu.sdsu.cs.ramya.rssreader.adapter.SettingsListAdapter;


public class SettingsActivity extends ActionBarActivity
{

    private ListView settingsListView;
    private static final String COUNT_SAVED_KEY = "feedCount";
    public static final String PREFS = "preferencesKey";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingsListView = (ListView)findViewById(R.id.settingsListView);
        settingsListView.setDividerHeight(4);
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showSettingsDialog();
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SharedPreferences prefs = this.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int toSelectPosition = prefs.getInt(COUNT_SAVED_KEY,-1);
        int feedCount;
        if(toSelectPosition == 5)
        {
            feedCount = 100;
        }
        else if(toSelectPosition == -1)
        {
            feedCount = 100;
        }
        else
        {
            String[] counts = this.getResources().getStringArray(R.array.feeds_count);

            feedCount = Integer.parseInt(counts[toSelectPosition]);
        }

        settingsListView.setAdapter(new SettingsListAdapter(this,0, feedCount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog()
    {
        SettingsDialogFragment dialog = new SettingsDialogFragment();
        dialog.show(getSupportFragmentManager(),"dialog");
    }

}
