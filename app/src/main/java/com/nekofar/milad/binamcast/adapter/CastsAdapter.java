package com.nekofar.milad.binamcast.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.model.Cast;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmResults;

public class CastsAdapter extends RecyclerView.Adapter<CastsAdapter.ViewHolder> {

    private Context mContext;
    private RealmResults<Cast> mCasts;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.title)
        public TextView mCastTitle;

        @InjectView(R.id.image)
        public ImageView mCastImage;

        public ViewHolder(View itemView) {
            super(itemView);

            //
            ButterKnife.inject(this, itemView);



        }
    }

    public void setCasts(RealmResults<Cast> casts) {
        this.mCasts = casts;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cast_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cast cast = mCasts.get(position);
        holder.mCastTitle.setText(cast.getName());
        Picasso.with(mContext).load(cast.getImage()).into(holder.mCastImage);
    }

    @Override
    public int getItemCount() {
        return mCasts.size();
    }

}
