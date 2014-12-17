package com.nekofar.milad.binamcast.event;

import android.view.View;

import com.nekofar.milad.binamcast.model.Cast;

public class PlayCastEvent {

    private static final String TAG = PlayCastEvent.class.getSimpleName();

    private Cast mCast;
    private View mView;

    public PlayCastEvent(Object object) {
        mView = (View) object;
        mCast = (Cast) mView.getTag();
    }

    public View getView() {
        return mView;
    }

    public Cast getCast() {
        return mCast;
    }
}
