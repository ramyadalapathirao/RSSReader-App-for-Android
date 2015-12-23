package edu.sdsu.cs.ramya.rssreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import edu.sdsu.cs.ramya.rssreader.adapter.RSSItemAdapter;


public class RSSItemsListFragment extends ListFragment implements AbsListView.OnScrollListener
{
    Context context;
    RSSItemAdapter rssAdapter;
    private OnSwipeRefreshStatus swipeRefreshEnabler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getListView().setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        getListView().setPadding(0,0,10,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View containerView=super.onCreateView(inflater, container, savedInstanceState);
        context=container.getContext();
        return containerView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Intent intent=new Intent(getActivity(),WebViewPagerActivity.class);
        intent.putExtra("currentItemPosition",position);
        intent.putExtra("title",(String) getActivity().getTitle());
        intent.putExtra("feedUrl",RSSItemsSingleton.get(getActivity()).
                getRSSItems().get(position).getLink());
        startActivity(intent);
        super.onListItemClick(l, v, position, id);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        rssAdapter =new RSSItemAdapter(getActivity(),0,RSSItemsSingleton.get(getActivity())
                                                                        .getRSSItems());
        getListView().setDividerHeight(2);
        setListAdapter(rssAdapter);
        getListView().setOnScrollListener(this);
    }

   public void refreshData()
   {
       rssAdapter.notifyDataSetChanged();
   }

    public void getFilter(String queryText)
    {
        if(rssAdapter !=null)
        {
            rssAdapter.getFilter().filter(queryText);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        swipeRefreshEnabler =(OnSwipeRefreshStatus)activity;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount)
    {
        boolean enable=false;
        if(getListView() != null && getListView().getChildCount() > 0)
        {
            boolean firstItemVisible = getListView().getFirstVisiblePosition() == 0;
            boolean topOfFirstItemVisible = getListView().getChildAt(0).getTop() == 0;
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        swipeRefreshEnabler.changeSwipeLayoutStatus(enable);
    }

    public  interface OnSwipeRefreshStatus
    {
        public void changeSwipeLayoutStatus(boolean status);
    }
}
