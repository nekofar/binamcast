package com.nekofar.milad.binamcast.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.common.Binamcast;
import com.nekofar.milad.binamcast.event.DownloadCastEvent;
import com.nekofar.milad.binamcast.model.Cast;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;
import io.realm.RealmResults;

public class CastsAdapter extends RecyclerView.Adapter<CastsAdapter.ViewHolder> implements View.OnClickListener {

    @Inject
    public Bus mBus;

    private Context mContext;
    private RealmResults<Cast> mCasts;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.title)
        public TextView mCastTitle;

        @InjectView(R.id.image)
        public ImageView mCastImage;

        public ViewHolder(View itemView) {
            super(itemView);

            // Inject ButterKnife
            ButterKnife.inject(this, itemView);
        }

    }

    @DebugLog
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

        holder.mCastTitle.setText(cast.getName());

        //
        holder.mCastImage.setTag(cast);
        holder.mCastImage.setOnClickListener(this);

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
        mBus.post(new DownloadCastEvent(view.getTag()));
    }

}
