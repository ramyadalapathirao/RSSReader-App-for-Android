package edu.sdsu.cs.ramya.rssreader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;

import edu.sdsu.cs.ramya.rssreader.R;
import edu.sdsu.cs.ramya.rssreader.RSSItem;
import edu.sdsu.cs.ramya.rssreader.RSSItemsSingleton;


public class RSSItemAdapter extends ArrayAdapter<RSSItem>
{
    private ArrayList<RSSItem> rssItemsArray;
    private Filter filter;
    private Context context;

    public RSSItemAdapter(Context context, int resource, ArrayList<RSSItem> objects) {
        super(context,0,objects);
        this.rssItemsArray = objects;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return RSSItemsSingleton.get(context).getRSSItems().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)getContext().
                                     getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.feed_item_row,parent,false);
            TextView titleTextView=(TextView)convertView.findViewById(R.id.titleTextView);
            TextView subtitleTextView=(TextView)convertView.findViewById(R.id.subtitleTextView);
            titleTextView.setText(getItem(position).getTitle());
            try {
                if(getItem(position).getSubTitle() != null)
                {
                    subtitleTextView.setText(getItem(position).getSubTitle());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
        {
            TextView titleTextView=(TextView)convertView.findViewById(R.id.titleTextView);
            TextView subtitleTextView=(TextView)convertView.findViewById(R.id.subtitleTextView);
            titleTextView.setText(getItem(position).getTitle());
            try
            {
                if(getItem(position).getSubTitle() != null)
                {
                    subtitleTextView.setText(getItem(position).getSubTitle());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return convertView;
    }
    public Filter getFilter()
    {
        if (filter == null)
            filter = new AdapterFilter(rssItemsArray);
        return filter;
    }

    private class AdapterFilter extends Filter
    {
        private ArrayList<RSSItem> filteredRSSItems;
        public AdapterFilter(ArrayList<RSSItem> objects)
        {
            filteredRSSItems =new ArrayList<>();
            filteredRSSItems.addAll(objects);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {

            String filterSeq = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0)
            {
                ArrayList<RSSItem> filter = new ArrayList<>();
                for (RSSItem object : filteredRSSItems)
                {
                    if (object.toString().toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            }
            else
            {
                synchronized (this)
                {
                    result.values = filteredRSSItems;
                    result.count = filteredRSSItems.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            ArrayList<RSSItem> filtered = (ArrayList<RSSItem>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
            {
                add(filtered.get(i));
            }
            notifyDataSetInvalidated();
        }
    }
}
