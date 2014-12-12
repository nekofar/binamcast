package com.nekofar.milad.binamcast.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.adapter.CastsAdapter;
import com.nekofar.milad.binamcast.common.Binamcast;
import com.nekofar.milad.binamcast.event.DownloadCastEvent;
import com.nekofar.milad.binamcast.event.PlayCastEvent;
import com.nekofar.milad.binamcast.event.UpdateCastsEvent;
import com.nekofar.milad.binamcast.model.Cast;
import com.nekofar.milad.binamcast.model.Entry;
import com.nekofar.milad.binamcast.model.Feed;
import com.nekofar.milad.binamcast.utility.FeedService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    protected Bus mBus;

    @Inject
    protected FeedService mFeedService;

    @Inject
    protected Realm mRealm;

    @InjectView(R.id.casts)
    protected RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Injecting ButterKnife
        ButterKnife.inject(this);

        // Injecting Dagger modules
        ((Binamcast) getApplication()).getObjectGraph().inject(this);

        // Get number of columns for grid view
        int columnCount = getResources().getInteger(R.integer.casts_column_count);

        // Initialize RecyclerView list
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));

        // Set animation for RecyclerView
        DefaultItemAnimator animator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(animator);

        // Get the list of casts sorted by date
        RealmResults<Cast> casts = mRealm.where(Cast.class).findAll();
        casts.sort("date", RealmResults.SORT_ORDER_DESCENDING);

        // Initialize CastsAdapter to populate RecyclerView
        CastsAdapter adapter = new CastsAdapter();
        adapter.setCasts(casts);
        adapter.setContext(MainActivity.this);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register Otto event bus
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister Otto event bus
        mBus.unregister(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // Attach context using CalligraphyContextWrapper to set default fonts
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu layout
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle option menu actions
        switch (item.getItemId()) {
            case R.id.update_casts:
                mBus.post(new UpdateCastsEvent()); return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Subscribe
    public void doPlayCast(PlayCastEvent event) {
        Cast cast = event.getCast();

        //

    }

    @Subscribe
    public void doDownloadCast(DownloadCastEvent event) {
        Cast cast = event.getCast();

        // Get url of file from cast and pars it
        Uri uri = Uri.parse(cast.getFile());
        String fileName = uri.getLastPathSegment();
        fileName = fileName.replaceAll(" ", "_");
        fileName = fileName.replaceAll("[\"|\\\\?*<\":>+\\[\\]/']", "");

        // Create download manager request using url
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(cast.getName());
        request.setDescription(getString(R.string.app_name));
        request.setDestinationInExternalPublicDir("/download/", fileName);

        // Using DownloadManager for download cast file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Subscribe
    public void doUpdateCasts(UpdateCastsEvent event) {
        // Create new instance of Realm
        mRealm.beginTransaction();

        // Try to update Casts using service
        mFeedService.getFeed(new Callback<Feed>() {
            @Override
            public void success(Feed feed, Response response) {

                List<Entry> entries = feed.getEntries();
                for (Entry entry : entries) {

                    Log.v(TAG, entry.getId());

                    // Try to prevent duplication of Casts
                    Cast cast = null;
                    cast = mRealm.where(Cast.class)
                            .equalTo("id", entry.getId())
                            .findFirst();

                    if (cast == null) {
                        // Create new Cast object
                        cast = mRealm.createObject(Cast.class);
                        cast.setId(entry.getId());
                        cast.setName(entry.getTitle());
                        cast.setText(entry.getContent());
                        cast.setDate(entry.getPublished());
                        cast.setLink(entry.getLinks().get("alternate"));
                        //cast.setFile(entry.getLinks().get("enclosure"));
                        cast.setFile("http://192.168.101.50/Podcast_Binam_07_128.mp3");

                        // Extract image link from feed content
                        String image = entry.getContent();
                        String string = "images&#093;&#091;0&#093;="
                                + "(http%3A%2F%2Fbinamcast\\.ir.[^=]*\\.(jpg|png))"
                                + "&#038;p&#091;";
                        Pattern pattern = Pattern.compile(string);
                        Matcher matcher = pattern.matcher(image);
                        while (matcher.find()) image = matcher.group(1);
                        try { image = URLDecoder.decode(image, "UTF-8"); }
                        catch (UnsupportedEncodingException e) { e.printStackTrace(); }
                        cast.setImage(image);
                    }
                }

                // Commit Realm transaction
                mRealm.commitTransaction();
            }

            @Override
            public void failure(RetrofitError error) {
                // Cancel Realm transaction on fail request
                mRealm.cancelTransaction();
            }
        });
    }
}
