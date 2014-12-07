package com.nekofar.milad.binamcast.module;

import com.nekofar.milad.binamcast.utility.FeedService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;

@Module(library = true, complete = false)
public class RetrofitModule {

    @Provides
    @Singleton
    public FeedService getFeedService(RestAdapter restAdapter){
        return restAdapter
                .create(FeedService.class);
    }

    @Provides
    public RestAdapter provideRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint("http://binamcast.ir")
                .setConverter(new SimpleXMLConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

}
