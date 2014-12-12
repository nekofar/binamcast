package com.nekofar.milad.binamcast.event;

import com.nekofar.milad.binamcast.model.Cast;

public class PauseCastEvent {

    private static final String TAG = PauseCastEvent.class.getSimpleName();

    private Cast mCast;

    public PauseCastEvent(Object object) {
        mCast = (Cast) object;
    }

    public Cast getCast() {
        return mCast;
    }
}
