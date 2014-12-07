package com.nekofar.milad.binamcast.utility;

import com.nekofar.milad.binamcast.model.Feed;

import retrofit.Callback;
import retrofit.http.GET;

public interface FeedService {

    @GET("/feed")
    void getFeed(Callback<Feed> callback);

}
