package com.nekofar.milad.binamcast.module;

import android.content.Context;

import com.nekofar.milad.binamcast.common.ForApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module(library = true, complete = false)
public class RealmModule {

    @Provides
    @Singleton
    public Realm provideRealm(@ForApplication Context context) {
        return Realm.getInstance(context);
    }

}
