package edu.sdsu.cs.ramya.rssreader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import edu.sdsu.cs.ramya.rssreader.R;
import edu.sdsu.cs.ramya.rssreader.SubscribedFeedDetails;


public class GridItemAdapter extends BaseAdapter
{

    private Context activityContext;
    private String categoryTitle;
    private HashMap<String, List<SubscribedFeedDetails>> feedInfo;
    public GridItemAdapter(Context context, String header, HashMap<String,
                           List<SubscribedFeedDetails>> listChildData)
    {
        this.activityContext = context;
        this.feedInfo = listChildData;
        this.categoryTitle = header;
    }
    @Override
    public int getCount()
    {
        //grid items count
        return feedInfo.get(categoryTitle).size();
    }

    @Override
    public Object getItem(int position)
    {
        //returns subscribed feed details
        return feedInfo.get(categoryTitle).get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public String getCategoryTitle(int position)
    {
        return categoryTitle;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        LayoutInflater inflater = (LayoutInflater) activityContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SubscribedFeedDetails feedDetails = feedInfo.get(categoryTitle).get(position);
        View gridView = convertView;
        ViewHolder holder;
        if (gridView == null) {

            gridView = inflater.inflate(R.layout.grid_item_layout, null);
            holder = new ViewHolder();
            holder.gridFeedTitle =(TextView)gridView.findViewById(R.id.grid_feed_title);
            holder.gridFeedIcon = (ParseImageView)gridView.findViewById(R.id.grid_feed_icon);
            gridView.setTag(holder);
        }
        else
        {
            holder =(ViewHolder)gridView.getTag();
        }
        holder.gridFeedTitle.setText(feedDetails.getFeedTitle());
        holder.gridFeedIcon.setPlaceholder(activityContext.getResources().
                                                           getDrawable(R.drawable.placeholder));
        ParseFile imageFile= feedDetails.getFeedImage();

        if(imageFile == null)
        {
            Picasso.with(activityContext)
                    .load(R.drawable.ic_launcher)
                    .resize(50, 50)
                    .centerCrop()
                    .into(holder.gridFeedIcon);
        }
        else
        {
            Picasso.with(activityContext)
                    .load(imageFile.getUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .resize(50, 50)
                    .centerCrop()
                    .into(holder.gridFeedIcon);
        }
        return gridView;
    }

    private static class ViewHolder
    {
        ParseImageView gridFeedIcon;
        TextView gridFeedTitle;
    }


}
