package com.nekofar.milad.binamcast.common;

import android.app.Application;
import android.content.Context;

import com.nekofar.milad.binamcast.R;
import com.nekofar.milad.binamcast.activity.MainActivity;
import com.nekofar.milad.binamcast.adapter.CastsAdapter;
import com.nekofar.milad.binamcast.module.OttoModule;
import com.nekofar.milad.binamcast.module.RealmModule;
import com.nekofar.milad.binamcast.module.RetrofitModule;
import com.nekofar.milad.binamcast.receiver.DownloadReceiver;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Binamcast extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize calligraphy with font
        CalligraphyConfig.initDefault("BYekan.ttf", R.attr.fontPath);

        // TODO
        mObjectGraph = ObjectGraph.create(getModules().toArray());
        mObjectGraph.inject(this);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new SimpleModule(this));
    }

    public ObjectGraph getObjectGraph() {
        return this.mObjectGraph;
    }

    @Module(
            injects = {
                    Binamcast.class,
                    MainActivity.class,
                    CastsAdapter.class,
                    DownloadReceiver.class
            },
            includes = {
                    OttoModule.class,
                    RealmModule.class,
                    RetrofitModule.class
            }
    )
    public class SimpleModule {
        private Binamcast mApplication;

        public SimpleModule(Binamcast application) {
            this.mApplication = application;
        }

        @Provides
        @Singleton
        @ForApplication
        public Context provideApplicationContext() {
            return mApplication;
        }
    }

}
