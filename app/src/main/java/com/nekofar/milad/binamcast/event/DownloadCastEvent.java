package com.nekofar.milad.binamcast.event;

import com.nekofar.milad.binamcast.model.Cast;

/**
 * Created by milad on 12/11/14.
 */
public class DownloadCastEvent {
    Cast mCast;

    public DownloadCastEvent(Object object) {
        mCast = (Cast) object;
    }

    public Cast getCast() {
        return mCast;
    }

}
