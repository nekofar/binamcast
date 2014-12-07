package com.nekofar.milad.binamcast.common;

import android.app.Application;

import com.nekofar.milad.binamcast.activity.MainActivity;
import com.nekofar.milad.binamcast.module.OttoModule;
import com.nekofar.milad.binamcast.module.RetrofitModule;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.ObjectGraph;

public class Binamcast extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

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
                    MainActivity.class
            },
            includes = {
                    OttoModule.class,
                    RetrofitModule.class
            }
    )
    public class SimpleModule {
        private Binamcast mApplication;

        public SimpleModule(Binamcast application) {
            this.mApplication = application;
        }
    }

}
