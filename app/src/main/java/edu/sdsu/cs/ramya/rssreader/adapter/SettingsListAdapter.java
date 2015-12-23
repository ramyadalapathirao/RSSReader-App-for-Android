package edu.sdsu.cs.ramya.rssreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.squareup.picasso.Picasso;

import edu.sdsu.cs.ramya.rssreader.R;


public class SettingsListAdapter extends ArrayAdapter
{
    private Context context;
    private int maxItemsPerFeed;

    public SettingsListAdapter(Context context, int resource,int feedCount)
    {
        super(context, resource);
        this.context = context;
        this.maxItemsPerFeed = feedCount;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        ViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(R.layout.feed_item_row,parent,false);
            holder = new ViewHolder();
            holder.settingsTitle =(TextView)rowView.findViewById(R.id.titleTextView);
            holder.settingsTitle.setTextAppearance(context,
                                   android.R.style.TextAppearance_DeviceDefault_Medium);
            holder.settingsTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_hardware_keyboard_arrow_right, 0);
            holder.feedCount = (TextView)rowView.findViewById(R.id.subtitleTextView);
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }
            if(context != null) {
                holder.settingsTitle.setText(context.getResources().getString(R.string.item_count_setting));
            }
            if(maxItemsPerFeed == 100)
            {
                holder.feedCount.setText(context != null ?
                                   context.getResources().getString(R.string.all_items) : null);
            }
            else
            {
                holder.feedCount.setText("" + maxItemsPerFeed);
            }

        return rowView;
    }

    private static class ViewHolder
    {
        TextView settingsTitle;
        TextView feedCount;
    }


}
