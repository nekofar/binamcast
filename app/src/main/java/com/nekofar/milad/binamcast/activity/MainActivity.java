package com.nekofar.milad.binamcast.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.common.Binamcast;
import com.nekofar.milad.binamcast.model.Feed;
import com.nekofar.milad.binamcast.utility.FeedService;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    public FeedService mFeedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        ((Binamcast) getApplication()).getObjectGraph().inject(this);

        mFeedService.getFeed(new Callback<Feed>() {
            @Override
            public void success(Feed feed, Response response) {
                List<Feed.Channel.Item> items = feed.getChannel().getItems();
                for (Feed.Channel.Item item : items) {
                    Log.d(TAG, item.title);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
