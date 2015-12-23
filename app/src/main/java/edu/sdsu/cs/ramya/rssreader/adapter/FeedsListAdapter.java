package edu.sdsu.cs.ramya.rssreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.sdsu.cs.ramya.rssreader.R;


public class FeedsListAdapter extends ArrayAdapter
{
    private ArrayList<ParseObject> feedsList;
    View rowView;
    int resLayout;
    Context context;

    public FeedsListAdapter(Context context, int resource, ArrayList<ParseObject> feeds)
    {
        super(context, resource, feeds);
        this.feedsList = feeds;
        resLayout = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        rowView = convertView;
        ViewHolder holder;
        if(rowView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(resLayout,parent,false);
            holder = new ViewHolder();
            holder.feedTitle = (TextView) rowView.findViewById(R.id.feed_name);
            holder.feedIcon = (ParseImageView) rowView.findViewById(R.id.feed_icon);
            holder.checkBox = (CheckBox) rowView.findViewById(R.id.feedCheckBox);
            rowView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }
        ParseFile imageFile = null;
        holder.feedTitle.setText((String) feedsList.get(position).get("feed_title"));
        imageFile = feedsList.get(position).getParseFile("feed_image");
        holder.checkBox.setChecked(true);
        if(imageFile != null)
        {
           Picasso.with(context)
                   .load(imageFile.getUrl())
                   .placeholder(R.drawable.placeholder)
                   .error(R.drawable.placeholder)
                   .resize(50, 50)
                   .centerCrop()
                   .into(holder.feedIcon);

        }
        return rowView;
    }

    private static class ViewHolder
    {
        ParseImageView feedIcon;
        TextView feedTitle;
        CheckBox checkBox;
    }
}
