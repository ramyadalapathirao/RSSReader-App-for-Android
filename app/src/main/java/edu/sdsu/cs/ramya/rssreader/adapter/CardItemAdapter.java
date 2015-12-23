package edu.sdsu.cs.ramya.rssreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import edu.sdsu.cs.ramya.rssreader.CustomSubscriptionDBTask;
import edu.sdsu.cs.ramya.rssreader.SubscribedFeedsDBTask;
import edu.sdsu.cs.ramya.rssreader.UserSubscriptionsActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import edu.sdsu.cs.ramya.rssreader.CustomGridView;
import edu.sdsu.cs.ramya.rssreader.R;
import edu.sdsu.cs.ramya.rssreader.RSSItemsListActivity;
import edu.sdsu.cs.ramya.rssreader.SubscribedFeedDetails;


public class CardItemAdapter extends BaseAdapter {

    private Context context;
    private List<String> categoryTitlesList;
    int count = 0;
    private HashMap<String, List<SubscribedFeedDetails>> subscribedFeedDetailsMap;
    public CardItemAdapter(Context c, List<String> listHeader,
                           HashMap<String, List<SubscribedFeedDetails>> listChild)
    {
        this.context = c;
        this.categoryTitlesList = listHeader;
        this.subscribedFeedDetailsMap = listChild;
    }

    @Override
    public int getCount()
    {

        return categoryTitlesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return categoryTitlesList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        final ViewHolder holder;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.card_item_layout, null);
            holder = new ViewHolder();
            holder.cardCategoryTitle =(TextView)rowView.findViewById(R.id.grid_header);
            holder.cardGridView = (CustomGridView)rowView.findViewById(R.id.grid_view);
            rowView.setTag(holder);
        }
        else
        {
            holder =(ViewHolder)rowView.getTag();
        }

        holder.cardCategoryTitle.setText(categoryTitlesList.get(position));
        final GridItemAdapter gridAdapter=new GridItemAdapter(context,
                                        categoryTitlesList.get(position), subscribedFeedDetailsMap);

        holder.cardGridView.setAdapter(gridAdapter);
        holder.cardGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        holder.cardGridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            ArrayList<Integer> feedsPositions;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                                   boolean checked)
            {
                count = count + 1;
                if(count == 1)
                {
                    mode.setTitle(count+" item selected");
                }
                else
                {
                    mode.setTitle(count+" items selected");
                }
                if(checked)
                {
                    feedsPositions.add(position);
                }
                else
                {
                    feedsPositions.remove(Integer.valueOf(position));
                }

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                Spannable menuItem = new SpannableString("DELETE");
                menuItem.setSpan(new ForegroundColorSpan(Color.RED),0,menuItem.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                menu.add(0,0,0,menuItem).setIcon(R.drawable.ic_action_action_delete);
                feedsPositions = new ArrayList<>();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                removeItems();
                if(count == 1)
                {
                    Toast.makeText(context,count+" item removed", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, count + " items removed", Toast.LENGTH_SHORT).show();
                }
                count = 0;
                mode.finish();
                return true;
            }

            private void removeItems()
            {
                ArrayList<SubscribedFeedDetails> feedsToDelete= new ArrayList<>();
                String categoryTitle = gridAdapter.getCategoryTitle(0);
                for(int i=0;i<feedsPositions.size();i++)
                {
                    feedsToDelete.add(subscribedFeedDetailsMap.
                            get(categoryTitle).get(feedsPositions.get(i)));
                }

                for(int j=0;j<feedsToDelete.size();j++)
                {
                  Iterator<Map.Entry<String, List<SubscribedFeedDetails>>> listIterator =
                            subscribedFeedDetailsMap.entrySet().iterator();
                    while (listIterator.hasNext())
                    {
                        Map.Entry<String, List<SubscribedFeedDetails>> entry = listIterator.next();
                        if (entry.getValue().contains(feedsToDelete.get(j)))
                        {
                            entry.getValue().remove(feedsToDelete.get(j));
                            if (!feedsToDelete.get(j).isCustomFeed()) {
                                ArrayList<Integer> deleteFeeds = new ArrayList<>();
                                deleteFeeds.add(feedsToDelete.get(j).getFeedId());
                                ArrayList<Boolean> isFeedSubscribed = new ArrayList<>();
                                isFeedSubscribed.add(false);
                                SubscribedFeedsDBTask deleteSubscription =
                                        new SubscribedFeedsDBTask("subscriptionsInsert",
                                        context, deleteFeeds, isFeedSubscribed);
                                deleteSubscription.execute();
                            }
                            else
                            {
                                CustomSubscriptionDBTask deleteCustomFeeds =
                                        new CustomSubscriptionDBTask("customDelete",context,
                                                feedsToDelete.get(j).getFeedTitle(), null, null);
                                deleteCustomFeeds.execute();
                            }

                            if (entry.getValue().size() == 0)
                            {
                                categoryTitlesList.remove(categoryTitle);
                            }
                        }
                    }

                    gridAdapter.notifyDataSetChanged();
                    CardItemAdapter.this.notifyDataSetChanged();
                    updateSubscriptionStatus();
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                count = 0;
                feedsPositions = null;

            }
        });

        holder.cardGridView.setVerticalScrollBarEnabled(false);
        holder.cardGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItemAdapter adapter = (GridItemAdapter)parent.getAdapter();
                String categoryTitle = adapter.getCategoryTitle(0);
                Log.d("title",categoryTitle);
                String rssItemUrl = subscribedFeedDetailsMap.get(categoryTitle).get(position)
                                                                                    .getFeedUrl();
                String rssFeedTitle = subscribedFeedDetailsMap.get(categoryTitle).get(position)
                                                                                    .getFeedTitle();
                Intent showFeedItems = new Intent(context,RSSItemsListActivity.class);
                showFeedItems.putExtra("rssItemUrl", rssItemUrl);
                showFeedItems.putExtra("feedTitle", rssFeedTitle);
                if(context != null)
                {
                    context.startActivity(showFeedItems);
                }
            }
        });
        return rowView;
    }

    private void updateSubscriptionStatus()
    {
        if(categoryTitlesList.size() == 0)
        {
            if(context != null)
            {
                UserSubscriptionsActivity activityReference =
                        (UserSubscriptionsActivity)context;
                activityReference.displaySubscriptionsStatus();
            }
        }
    }

    public void refetchData(List<String> listHeader,
                            HashMap<String, List<SubscribedFeedDetails>> listChild)
    {
        this.categoryTitlesList.clear();
        this.subscribedFeedDetailsMap.clear();
        this.categoryTitlesList.addAll(listHeader);
        this.subscribedFeedDetailsMap = listChild;
        notifyDataSetChanged();
    }

    private static class ViewHolder
    {
        TextView cardCategoryTitle;
        CustomGridView cardGridView;
    }
}
