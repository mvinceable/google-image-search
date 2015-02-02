package com.example.mvince.gridimagesearch.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.example.mvince.gridimagesearch.adapters.ImageResultsAdapter;
import com.example.mvince.gridimagesearch.fragments.SettingsDialog;
import com.example.mvince.gridimagesearch.listeners.EndlessScrollListener;
import com.example.mvince.gridimagesearch.models.ImageResult;
import com.example.mvince.gridimagesearch.R;
import com.example.mvince.gridimagesearch.models.SearchSettings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity implements SettingsDialog.OnDataPass {
    private StaggeredGridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private SearchSettings searchSettings;
    private SearchView searchView;
    private String currentQuery = "";
    // For changing the background color dynamically
    boolean firstResults = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        // Creates the data source
        imageResults = new ArrayList<ImageResult>();
        // Attaches the data source to an adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        // Link the adapter to the adaterview (gridview)
        gvResults.setAdapter(aImageResults);
        searchSettings = new SearchSettings();
        // So soft keyboard doesn't automatically appear when viewing the grid again
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setupViews() {
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the image display activity
                // Creating an intent
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);

                // Get the image result to display
                ImageResult result = imageResults.get(position);

                // Pass image result into the intent
                i.putExtra("result", result); // need to either be serializable or parcelable

                // Launch the new activity
                startActivity(i);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("INFO", "onLoadMore: " + String.valueOf(page) + ", " + String.valueOf(totalItemsCount));
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //customLoadMoreDataFromApi(page);
                getPhotos(totalItemsCount);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                searchView.clearFocus();
                // perform query here
                getPhotos(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSearchSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSearchSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialog settingsDialog = new SettingsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("SearchSettings", searchSettings);
        settingsDialog.setArguments(bundle);
        settingsDialog.show(fm, "fragment_settings");
    }

    // Checks for network connectivity
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public void getPhotos(int offset) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_LONG).show();
            return;
        }

        // The api supports only 64 images, so return if we're asking for more than that
        if (offset >= 64) {
            return;
        } else if (offset == 0) {
            imageResults.clear(); // clear existing images from the array (in cases where it's a new search)
            aImageResults.notifyDataSetChanged();
        }

        // Return if query is empty
        if (currentQuery.length() <= 0) {
            return;
        }

        // https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
        AsyncHttpClient client = new AsyncHttpClient();
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + currentQuery + "&rsz=8" + searchSettings.getUrlParams();

        if (offset > 0) {
            searchUrl += "&start=" + String.valueOf(offset);
        }

        Log.i("INFO", "Search url: " + searchUrl);

        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    // When you make changes to the adapter, it does modify the underlying data
                    aImageResults.addAll(ImageResult.fromJsonArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("INFO", imageResults.toString());
                fadeBackgroundColorOnFirstResults();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(SearchActivity.this, "Failed to get search results", Toast.LENGTH_LONG).show();
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }
        });
    }

    // for first results screen, fade background to black
    private void fadeBackgroundColorOnFirstResults() {
        // Fade background from white to black
        if (firstResults) {
            RelativeLayout screen = (RelativeLayout) findViewById(R.id.activitySearch);
            ObjectAnimator colorFade = ObjectAnimator.ofObject(screen, "backgroundColor", new ArgbEvaluator(), Color.argb(255, 255, 255, 255), 0xff000000);
            colorFade.setDuration(500);
            colorFade.start();
            firstResults = false;
        }
    }

    @Override
    public void onDataPass(SearchSettings settings) {
        // Update current settings
        searchSettings = settings;
        getPhotos(0);
    }
}
