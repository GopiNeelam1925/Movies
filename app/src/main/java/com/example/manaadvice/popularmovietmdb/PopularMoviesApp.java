package com.example.manaadvice.popularmovietmdb;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.example.manaadvice.popularmovietmdb.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class PopularMoviesApp extends Application implements HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder().application(this).build().inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentAndroidInjector;
    }
}
