package edu.sdsu.cs.ramya.rssreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
    public static final String ALERT_TITLE_TAG = "alertTitle";
    public static final String ALERT_MESSAGE_TAG = "alertMessage";

    public static AlertDialogFragment newInstance(String alertTitle,String alertMessage)
    {
        Bundle args = new Bundle();
        args.putString(ALERT_TITLE_TAG, alertTitle);
        args.putString(ALERT_MESSAGE_TAG,alertMessage);
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String title = getArguments().getString(ALERT_TITLE_TAG);
        String message = getArguments().getString(ALERT_MESSAGE_TAG);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.settings_ok), this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }
}
