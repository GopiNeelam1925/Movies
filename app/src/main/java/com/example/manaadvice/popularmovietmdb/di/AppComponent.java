package com.example.manaadvice.popularmovietmdb.di;

import android.app.Application;

import com.example.manaadvice.popularmovietmdb.PopularMoviesApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class,
        ActivityBuilderModule.class,
        FragmentBuilderModule.class,
        AppModule.class,})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
    void inject(PopularMoviesApp app);
}
