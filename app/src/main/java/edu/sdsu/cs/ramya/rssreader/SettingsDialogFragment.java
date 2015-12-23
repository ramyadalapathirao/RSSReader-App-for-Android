package edu.sdsu.cs.ramya.rssreader;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingsDialogFragment extends DialogFragment
{
    private int selectedListPosition;
    private static final String COUNT_SAVED_KEY = "feedCount";
    public static final String PREFS = "preferencesKey";

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int initialListPosition = prefs.getInt(COUNT_SAVED_KEY, -1);
        if(initialListPosition == -1)
        {
            initialListPosition = 5;
        }
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(getResources().getString(R.string.item_count_setting));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.settings_dialog_list, null);
        String[] counts = getActivity().getResources().getStringArray(R.array.feeds_count);
        final List<String> feedsCountsList = new ArrayList<String>(Arrays.asList(counts));
        final ListView list = (ListView)view.findViewById(R.id.dialogListView);
        Button getFeedCount = (Button) view.findViewById(R.id.selectCount);
        Button cancelButton = (Button) view.findViewById(R.id.cancelCount);
        selectedListPosition = initialListPosition;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedListPosition = position;
            }
        });
        getFeedCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SharedPreferences preferences = getActivity().getSharedPreferences(PREFS,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(COUNT_SAVED_KEY, selectedListPosition);
                editor.apply();
                getActivity().finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.setContentView(view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice,feedsCountsList);
        list.setAdapter(adapter);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setItemChecked(initialListPosition, true);
        return dialog;
    }
}