package com.nekofar.milad.binamcast.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.common.Binamcast;
import com.nekofar.milad.binamcast.event.RefreshCastsEvent;
import com.nekofar.milad.binamcast.model.Cast;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import io.realm.Realm;

public class DownloadReceiver extends BroadcastReceiver {

    private static final String TAG = DownloadReceiver.class.getSimpleName();

    @Inject
    protected Bus mBus;

    @Inject
    protected Realm mRealm;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Injecting Dagger modules
        ((Binamcast) context.getApplicationContext())
                .getObjectGraph().inject(this);

        // Get access to the DownloadManager service
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

        // Using
        Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(downloadId));
        cursor.moveToFirst();
        if (!cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)).equals(context.getString(R.string.app_name))) {
            Log.d(TAG, "Not our app!");
        }
        else if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            // Find cast using it's url
            Cast cast = mRealm.where(Cast.class)
                    .equalTo("file", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI)))
                    .findFirst();

            // Set save path of file for cast
            mRealm.beginTransaction();
            cast.setPath(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            mRealm.commitTransaction();

            // Refresh casts list after successful download
            mBus.post(new RefreshCastsEvent());

            Log.d(TAG, cast.getPath());
        }

    }

}
