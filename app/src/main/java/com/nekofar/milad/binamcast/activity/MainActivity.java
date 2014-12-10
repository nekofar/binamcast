package com.nekofar.milad.binamcast.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.adapter.CastsAdapter;
import com.nekofar.milad.binamcast.common.Binamcast;
import com.nekofar.milad.binamcast.model.Cast;
import com.nekofar.milad.binamcast.model.Entry;
import com.nekofar.milad.binamcast.model.Feed;
import com.nekofar.milad.binamcast.utility.FeedService;

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

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    public FeedService mFeedService;

    @Inject
    public Realm mRealm;

    @InjectView(R.id.casts)
    public RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private DefaultItemAnimator mItemAnimator;

    private CastsAdapter mCastsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        ButterKnife.inject(this);

        //
        ((Binamcast) getApplication()).getObjectGraph().inject(this);

        //
        mRecyclerView.setHasFixedSize(true);

        //
        int columnCount = getResources().getInteger(R.integer.casts_column_count);

        //
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager = new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //
        mItemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(mItemAnimator);

        //
        RealmResults<Cast> casts = mRealm.where(Cast.class).findAll();
        casts.sort("date", RealmResults.SORT_ORDER_DESCENDING);

        //
        mCastsAdapter = new CastsAdapter();
        mCastsAdapter.setCasts(casts);
        mCastsAdapter.setContext(MainActivity.this);
        mRecyclerView.setAdapter(mCastsAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle option menu actions
        switch (item.getItemId()) {
            case R.id.update_casts:
                this.doUpdateCasts(); return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void doUpdateCasts() {
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
