package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.sdsu.cs.ramya.rssreader.adapter.CategoriesListAdapter;

public class BrowseCategoriesFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private ListView categoriesListView;
    private ArrayList<ParseObject> categoriesArray;
    private ProgressBar spinner;
    private TextView loadingLabel;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                                      @Nullable Bundle savedInstanceState)
    {
        View browseLayout = inflater.inflate(R.layout.fragment_browse_categories,container,false);
        categoriesListView =(ListView) browseLayout.findViewById(R.id.list);
        categoriesListView.setDividerHeight(2);
        spinner = (ProgressBar)browseLayout.findViewById(R.id.progressBar);
        loadingLabel = (TextView)browseLayout.findViewById(R.id.loadingLabel);
        return browseLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        categoriesListView.setOnItemClickListener(this);
        fetchCategoriesList();
    }

    private void fetchCategoriesList()
    {
        ParseQuery<ParseObject> getCategoriesQuery = ParseQuery.getQuery("category");
        getCategoriesQuery.selectKeys(Arrays.asList
                                             ("category_id", "category_name", "category_image"));
        getCategoriesQuery.orderByAscending("category_id");
        getCategoriesQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        if(getCategoriesQuery.hasCachedResult() || isOnline())
        {
            spinner.setVisibility(View.VISIBLE);
            loadingLabel.setText(getString(R.string.loading));
            loadingLabel.setVisibility(View.VISIBLE);
        }
        getCategoriesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null)
                {
                    categoriesArray= new ArrayList<>();
                    categoriesArray.addAll(parseObjects);
                    if(getActivity() !=null)
                    {
                        CategoriesListAdapter listAdapter = new CategoriesListAdapter(getActivity(),
                                                                  categoriesArray);
                        categoriesListView.setAdapter(listAdapter);
                        categoriesListView.invalidateViews();
                        spinner.setVisibility(View.GONE);
                        loadingLabel.setVisibility(View.GONE);
                    }
                }
                else
                {
                    if(e.getMessage().equals("results not cached") && !isOnline())
                    {
                        RelativeLayout.LayoutParams layoutParams =
                                (RelativeLayout.LayoutParams)loadingLabel.getLayoutParams();
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        loadingLabel.setLayoutParams(layoutParams);
                        loadingLabel.setVisibility(View.VISIBLE);
                        loadingLabel.setTextAppearance(getActivity(),
                                                         android.R.style.TextAppearance_Large);
                        loadingLabel.setText(getString(R.string.no_offline_categories_message));
                    }
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent showFeeds = new Intent(getActivity(),BrowseFeedsActivity.class);
        showFeeds.putExtra("categoryId",categoriesArray.get(position).getInt("category_id"));
        showFeeds.putExtra("categoryTitle",categoriesArray.get(position).getString("category_name"));
        startActivity(showFeeds);
    }

    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
