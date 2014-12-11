package com.nekofar.milad.binamcast.event;

import com.nekofar.milad.binamcast.model.Cast;

public class DownloadCastEvent {

    private static final String TAG = DownloadCastEvent.class.getSimpleName();

    private Cast mCast;

    public DownloadCastEvent(Object object) {
        mCast = (Cast) object;
    }

    public Cast getCast() {
        return mCast;
    }

}
