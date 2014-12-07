package com.nekofar.milad.binamcast.common;

import android.app.Application;

import com.nekofar.milad.binamcast.activity.MainActivity;
import com.nekofar.milad.binamcast.module.OttoModule;
import com.nekofar.milad.binamcast.module.RetrofitModule;

import dagger.Module;
import dagger.ObjectGraph;

public class BinamcastApplication extends Application {

    ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(new SimpleModule());
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    @Module(
            includes = {
                    OttoModule.class,
                    RetrofitModule.class
            },
            injects = {
                    MainActivity.class
            }
    )
    private class SimpleModule {
        public SimpleModule() {

        }
    }
}
