package com.nekofar.milad.binamcast.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.IconicsButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.common.Binamcast;
import com.nekofar.milad.binamcast.event.DownloadCastEvent;
import com.nekofar.milad.binamcast.event.PauseCastEvent;
import com.nekofar.milad.binamcast.event.PlayCastEvent;
import com.nekofar.milad.binamcast.model.Cast;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;
import io.realm.RealmResults;

public class CastsAdapter extends RecyclerView.Adapter<CastsAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = CastsAdapter.class.getSimpleName();

    @Inject
    public Bus mBus;

    private Context mContext;
    private RealmResults<Cast> mCasts;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.title)
        protected TextView mCastTitle;

        @InjectView(R.id.image)
        protected ImageView mCastImage;

        @InjectView(R.id.play)
        protected IconicsButton mCastPlay;

        @InjectView(R.id.pause)
        protected IconicsButton mCastPause;

        @InjectView(R.id.download)
        protected IconicsButton mCastDownload;

        public ViewHolder(View itemView) {
            super(itemView);

            // Inject ButterKnife
            ButterKnife.inject(this, itemView);
        }

    }

    public void setCasts(RealmResults<Cast> casts) {
        this.mCasts = casts;
    }

    public void setContext(Context context) {
        this.mContext = context;

        // Inject dagger modules
        ((Binamcast) mContext.getApplicationContext())
                .getObjectGraph().inject(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cast_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get current cast using position
        Cast cast = mCasts.get(position);

        // Set cast title to the row
        holder.mCastTitle.setText(cast.getName());

        // Set cast action buttons and listeners
        File file = new File(URI.create(cast.getPath()).getPath());
        if (file.exists() && file.isFile()) {
            holder.mCastPlay.setVisibility(View.VISIBLE);
            holder.mCastDownload.setVisibility(View.GONE);

            holder.mCastPlay.setTag(cast);
            holder.mCastPlay.setOnClickListener(this);
        } else {
            holder.mCastPlay.setVisibility(View.GONE);
            holder.mCastDownload.setVisibility(View.VISIBLE);

            holder.mCastDownload.setTag(cast);
            holder.mCastDownload.setOnClickListener(this);
        }

        // Set cast image using Picasso
        Picasso.with(mContext).load(cast.getImage()).into(holder.mCastImage);
    }

    @Override
    public int getItemCount() {
        return mCasts.size();
    }

    @DebugLog
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                mBus.post(new PlayCastEvent(view.getTag()));
                break;
            case R.id.pause:
                mBus.post(new PauseCastEvent(view.getTag()));
                break;
            case R.id.download:
                mBus.post(new DownloadCastEvent(view.getTag()));
                break;
            default:
                break;
        }
    }

}
