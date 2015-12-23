package edu.sdsu.cs.ramya.rssreader.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.sdsu.cs.ramya.rssreader.R;


public class CategoriesListAdapter extends ArrayAdapter
{
    private Activity context;
    private ArrayList<ParseObject> categoriesList;
    public CategoriesListAdapter(Activity context, ArrayList<ParseObject> categories)
    {
        super(context, R.layout.categories_list_item);
        this.context = context;
        this.categoriesList = categories;
    }

    @Override
    public int getCount()
    {
        return categoriesList.size();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        View rowView = convertView;
        ViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView= inflater.inflate(R.layout.categories_list_item,parent,false);
            holder = new ViewHolder();
            holder.categoryTitle =(TextView)rowView.findViewById(R.id.category_title);
            holder.categoryIcon = (ParseImageView)rowView.findViewById(R.id.category_icon);
            rowView.setTag(holder);
        }
        else
        {
            holder =(ViewHolder)rowView.getTag();
        }
        ParseFile imageFile = null;
        holder.categoryTitle.setText((String) categoriesList.get(position).get("category_name"));
        imageFile = categoriesList.get(position).getParseFile("category_image");
        holder.categoryIcon.setPlaceholder(context.getResources().
                                           getDrawable(R.drawable.placeholder));
        if(imageFile != null)
        {
            Picasso.with(context)
                    .load(imageFile.getUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .resize(50, 50)
                    .centerCrop()
                    .into(holder.categoryIcon);
        }
        return rowView;
    }

    private static class ViewHolder {
        ParseImageView categoryIcon;
        TextView categoryTitle;
    }

}
