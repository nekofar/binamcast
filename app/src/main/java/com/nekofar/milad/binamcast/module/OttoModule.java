package com.nekofar.milad.binamcast.module;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class OttoModule {

    @Provides
    @Singleton
    public Bus provideBus() {
        return new Bus();
    }

}
