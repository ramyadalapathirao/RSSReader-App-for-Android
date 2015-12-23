package edu.sdsu.cs.ramya.rssreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class SearchFeedFragment extends Fragment implements View.OnClickListener
{

    private EditText customFeedName;
    private EditText customFeedURL;
    private Button addFeedButton;
    private Button cancelButton;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View searchLayout = inflater.inflate(R.layout.fragment_search_feed,container,false);
        customFeedName =(EditText) searchLayout.findViewById(R.id.websiteName);
        customFeedURL = (EditText) searchLayout.findViewById(R.id.inputLink);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.search_dialog_title));
        progressDialog.setMessage(getString(R.string.search_dialog_message));
        progressDialog.setIndeterminate(false);
        addFeedButton = (Button)searchLayout.findViewById(R.id.addFeed);
        cancelButton = (Button)searchLayout.findViewById(R.id.cancelButton);
        ScrollView scrollView = (ScrollView) searchLayout.findViewById(R.id.scrollView);
        setListenersToViews();
        setupUI(scrollView);
        return searchLayout;
    }

    public void setupUI(View view)
    {

        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyBoard();
                    return false;
                }

            });
        }
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    private void setListenersToViews()
    {
        addFeedButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        customFeedName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                customFeedName.setError(null);
            }
        });
        customFeedURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    customFeedURL.setError(null);
            }
        });
    }
    private void hideKeyBoard()
    {
        View view = getActivity().getCurrentFocus();
        if(view != null)
        {
            InputMethodManager inputManager = (InputMethodManager)getActivity().
                                               getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.addFeed)
        {
            if(!isOnline())
            {
                if(getActivity() != null) {
                    AlertDialogFragment alertDialog = AlertDialogFragment.
                            newInstance(getString(R.string.network_error),
                                        getString(R.string.search_feed_offline_error));
                    alertDialog.show(getFragmentManager(),"alert");
                }
            }
            else
            {
                subscribe();
            }

        }
        if(v.getId() == R.id.cancelButton)
        {
            cancel();
        }

    }

    private void cancel()
    {
        customFeedName.setError(null);
        customFeedURL.setError(null);
        hideKeyBoard();
        customFeedName.setText("");
        customFeedURL.setText("");
    }

    private void subscribe()
    {
        hideKeyBoard();
        String inputUrl = customFeedURL.getText().toString().trim().toLowerCase();
        String inputWebsiteName = customFeedName.getText().toString().trim();
        if(inputWebsiteName.equals(""))
        {
            customFeedName.setError(getString(R.string.feed_title_error));
            customFeedName.invalidate();
        }
        else if(inputUrl.equals(""))
        {
            customFeedURL.setError(getString(R.string.feed_url_error));
            customFeedURL.invalidate();
        }
        else
        {
            customFeedName.setError(null);
            customFeedURL.setError(null);
            if (!(inputUrl.startsWith("http", 0)) && !(inputUrl.startsWith("https", 0))) {
                inputUrl = "http://" + inputUrl;
            }
            findRSSLink(inputUrl);
        }
    }

    private void findRSSLink(String url)
    {
        String mimeType = url.substring(url.length()-3);
        if(mimeType.equalsIgnoreCase("xml"))
        {
            addCustomFeedToDatabase(url);
        }
        else {
            new HtmlTask().execute(url);
        }
    }

    private class  HtmlTask extends AsyncTask<String,Void,Void>
    {
        String linkFound ="";
        String absoluteURL = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            absoluteURL = params[0];
            try {
                Connection.Response response = Jsoup.connect(absoluteURL)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) " +
                                "Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com")
                        .timeout(12000)
                        .followRedirects(true)
                        .execute();
                Document doc = response.parse();
                Elements links=doc.select("link");
                for (Element linkElement : links)
                {
                    String relation=linkElement.attr("rel");
                    if(relation.equalsIgnoreCase("alternate"))
                    {
                        String type=linkElement.attr("type");
                        if(type.equalsIgnoreCase("application/rss+xml")) {
                            linkFound = linkElement.attr("href");
                            break;
                        }
                        if(type.equalsIgnoreCase("application/atom+xml"))
                        {
                            linkFound = linkElement.attr("href");
                            break;
                        }
                    }
                }
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(linkFound.equals(""))
            {
                progressDialog.dismiss();
                String feedName = customFeedName.getText().toString();
                customFeedName.setText("");
                customFeedURL.setText("");
                AlertDialogFragment dialog = AlertDialogFragment.newInstance("",
                                                      getString(R.string.no_rss_link,feedName));
                dialog.show(getFragmentManager(),"alert");
            }
            else
            {
                if(linkFound.startsWith("/",0))
                {
                    linkFound = absoluteURL+linkFound;
                }
                addCustomFeedToDatabase(linkFound);
            }

        }
    }

    private void addCustomFeedToDatabase(String rssLink)
    {
        if(getActivity() != null)
        {
            CustomSubscriptionDBTask dbTask = new CustomSubscriptionDBTask("customInsert",
                                                                           getActivity(),
                    customFeedName.getText().toString(), rssLink,this);
            dbTask.execute();

        }
    }

    public void customFeedInserted()
    {
        progressDialog.dismiss();
        Toast.makeText(getActivity(),getString(R.string.custom_subscription_success,
                        customFeedName.getText().toString()),Toast.LENGTH_SHORT).show();
        customFeedName.setText("");
        customFeedURL.setText("");
        Intent goBack = new Intent(getActivity(),UserSubscriptionsActivity.class);
        goBack.putExtra("title",getString(R.string.custom_category_title));
        goBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBack);
    }
    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }



}
