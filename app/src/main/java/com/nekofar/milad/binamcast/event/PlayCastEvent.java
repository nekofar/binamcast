package com.nekofar.milad.binamcast.event;

import com.nekofar.milad.binamcast.model.Cast;

public class PlayCastEvent {

    private static final String TAG = PlayCastEvent.class.getSimpleName();

    private Cast mCast;

    public PlayCastEvent(Object object) {
        mCast = (Cast) object;
    }

    public Cast getCast() {
        return mCast;
    }
}
