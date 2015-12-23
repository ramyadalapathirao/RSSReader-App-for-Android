package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class WebViewPagerFragment extends Fragment
{
    private WebView feedWebView;
    private ProgressBar progressBar;
    private TextView loadingLabel;
    private boolean isPageError;
    private Bundle webViewBundle;
    CustomWebChromeClient webChromeClient;
    CustomWebViewClient webViewClient;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_webview,container,false);
        feedWebView = (WebView)v.findViewById(R.id.webView);
        feedWebView.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        loadingLabel = (TextView)v.findViewById(R.id.loadingLabel);
        webChromeClient = new CustomWebChromeClient();
        webViewClient = new CustomWebViewClient();
        initializeUI();
        return v;
    }

    private void initializeUI()
    {
        setWebViewSettings();
        feedWebView.setWebChromeClient(webChromeClient);
        feedWebView.setWebViewClient(webViewClient);
        //Load a page
        if(webViewBundle != null)
        {
            feedWebView.restoreState(webViewBundle);
        }
        else
        {
            if(!isPageError)
            {
                String urlToLoad = getArguments().getString("url");
                feedWebView.loadUrl(urlToLoad);
            }
        }
    }

    private void setWebViewSettings()
    {
        String userAgent ="Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 " +
                          "Firefox/4.0";
        feedWebView.getSettings().setUserAgentString(userAgent);
        feedWebView.getSettings().setSupportZoom(true);
        feedWebView.getSettings().setBuiltInZoomControls(true);
        feedWebView.getSettings().setAppCachePath(getActivity().getCacheDir().getAbsolutePath());
        feedWebView.getSettings().setAllowFileAccess( true );
        feedWebView.getSettings().setAppCacheEnabled( true );
        if(!isOnline())
        {
            feedWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }
        else
        {
            feedWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        feedWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(feedWebView != null) {
            feedWebView.onResume();
        }
        //feedWebView.getSettings().setJavaScriptEnabled(true);
    }

    public static WebViewPagerFragment newInstance(String url,String title)
    {
        WebViewPagerFragment webFragment=new WebViewPagerFragment();
        Bundle args=new Bundle();
        args.putString("url",url);
        args.putString("title",title);
        webFragment.setArguments(args);
        return webFragment;
    }
    public boolean isOnline()
    {
        ConnectivityManager cm =
                 (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        webViewBundle = new Bundle();
        if(feedWebView != null)
        {
            feedWebView.onPause();
            feedWebView.saveState(webViewBundle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        feedWebView.saveState(outState);
    }

    public boolean canGoBack()
    {
        return feedWebView != null && feedWebView.canGoBack();
    }

    public void goBack()
    {
        if(feedWebView != null)
        {
            feedWebView.goBack();
        }

    }

    public class CustomWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            if (errorCode == -1)
            {
                isPageError = true;
                progressBar.setVisibility(View.GONE);
                loadingLabel.setVisibility(View.GONE);
                feedWebView.stopLoading();
                feedWebView.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)loadingLabel.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                loadingLabel.setLayoutParams(layoutParams);
                loadingLabel.setText(getString(R.string.webView_offline_message));
                loadingLabel.setVisibility(View.VISIBLE);
                return;
            }
            isPageError = false;
            super.onReceivedError(view, errorCode, description, failingUrl);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event)
        {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                feedWebView.goBack();
            }
            return true;
        }
    }

    public class CustomWebChromeClient extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            getActivity().setTitle(getResources().getString(R.string.loading));
            getActivity().setProgress(newProgress * 100);

            if (newProgress == 100)
            {
                getActivity().setTitle(getArguments().getString("title"));
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(feedWebView != null)
        {
            feedWebView.stopLoading();
            feedWebView.setWebChromeClient(null);
            feedWebView.setWebViewClient(null);
            feedWebView.destroy();
            feedWebView = null;
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        if(feedWebView != null)
        {
            feedWebView.removeAllViews();
            feedWebView.destroy();
        }
    }
}
