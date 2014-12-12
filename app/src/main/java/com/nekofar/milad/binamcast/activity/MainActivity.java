package com.nekofar.milad.binamcast.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
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
import com.nekofar.milad.binamcast.event.InstallEvent;
import com.nekofar.milad.binamcast.event.PauseCastEvent;
import com.nekofar.milad.binamcast.event.PlayCastEvent;
import com.nekofar.milad.binamcast.event.RefreshCastsEvent;
import com.nekofar.milad.binamcast.event.UpdateCastsEvent;
import com.nekofar.milad.binamcast.model.Cast;
import com.nekofar.milad.binamcast.model.Entry;
import com.nekofar.milad.binamcast.model.Feed;
import com.nekofar.milad.binamcast.utility.FeedService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private RealmResults<Cast> mCasts;
    private CastsAdapter mCastsAdapter;
    private MediaPlayer mMediaPlayer;
    private SharedPreferences mPreferences;

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
        mCasts = mRealm.where(Cast.class).findAll();
        mCasts.sort("date", RealmResults.SORT_ORDER_DESCENDING);

        // Initialize CastsAdapter to populate RecyclerView
        mCastsAdapter = new CastsAdapter();
        mCastsAdapter.setCasts(mCasts);
        mCastsAdapter.setContext(MainActivity.this);
        mRecyclerView.setAdapter(mCastsAdapter);

        // Customize acton bar layout
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        // Create new instance of media player
        mMediaPlayer = new MediaPlayer();

        // Get SharedPreferences instance for storing sign-up state
        mPreferences = getSharedPreferences("default", Context.MODE_PRIVATE);
        if (!mPreferences.getBoolean("alreadyInstalled", false)) {
            mBus.post(new InstallEvent());
        }
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
    protected void onDestroy() {
        super.onDestroy();

        //
        mMediaPlayer.release();
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
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(Uri.parse(cast.getPath()).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void doPauseCast(PauseCastEvent event) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Subscribe
    public void doDownloadCast(DownloadCastEvent event) {
        Cast cast = event.getCast();

        // Get url of file from cast and pars it
        Uri uri = Uri.parse(cast.getFile());
        String fileName = uri.getLastPathSegment();
        fileName = fileName.replaceAll(" ", "_");
        fileName = fileName.replaceAll("[\"|\\\\?*<\":>+\\[\\]/']", "");

        // Check if cast file not exist and then start download
        String filePath = Environment.getExternalStorageDirectory() + "/download/" + fileName;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {

            // Create download manager request using url
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(cast.getName());
            request.setDescription(getString(R.string.app_name));
            request.setDestinationInExternalPublicDir("/download/", fileName);

            // Using DownloadManager for download cast file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

        }
        else {
            // Refresh casts list if cast file exist
            mBus.post(new RefreshCastsEvent());
        }

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
                        cast.setFile(entry.getLinks().get("enclosure"));

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

                // Refresh casts list after successful request
                mBus.post(new RefreshCastsEvent());

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

    @Subscribe
    public void doRefreshCasts(RefreshCastsEvent event) {
        // Update CastsAdapter to re-populate RecyclerView
        mCastsAdapter = new CastsAdapter();
        mCastsAdapter.setCasts(mCasts);
        mCastsAdapter.setContext(MainActivity.this);
        mRecyclerView.setAdapter(mCastsAdapter);
    }

    @Subscribe
    public void doInstallEvent(InstallEvent event) {
        // Begin of new Realm transaction
        mRealm.beginTransaction();

        // Read string from file and convert to json string
        String jsonString = "";
        try {
            InputStream inputStream = getAssets().open("data.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            // Cancel Realm transaction on fail request
            mRealm.cancelTransaction();

            e.printStackTrace();
        }

        // Convert json string to the json array
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);

            // Read json array data and write to the database
            for (int i = 0; i < jsonArray.length(); i++) {
                // Create new cast object and populate it
                Cast cast = mRealm.createObject(Cast.class);
                cast.setId(jsonArray.getJSONObject(i).getString("id"));
                cast.setName(jsonArray.getJSONObject(i).getString("name"));
                cast.setText(jsonArray.getJSONObject(i).getString("text"));
                cast.setDate(jsonArray.getJSONObject(i).getString("date"));
                cast.setLink(jsonArray.getJSONObject(i).getString("link"));
                cast.setFile(jsonArray.getJSONObject(i).getString("file"));
                cast.setImage(jsonArray.getJSONObject(i).getString("image"));
            }
        } catch (JSONException e) {
            // Cancel Realm transaction on fail request
            mRealm.cancelTransaction();

            e.printStackTrace();
        }

        // Commit Realm transaction
        mRealm.commitTransaction();

        // Set already installed to the true if data updated
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("alreadyInstalled", true);
        editor.apply();
    }

}
