package com.nekofar.milad.binamcast.event;

import android.view.View;

import com.nekofar.milad.binamcast.model.Cast;

public class PauseCastEvent {

    private static final String TAG = PauseCastEvent.class.getSimpleName();

    private Cast mCast;
    private View mView;

    public PauseCastEvent(Object object) {
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
