package com.example.manaadvice.popularmovietmdb.di;

import com.example.manaadvice.popularmovietmdb.ui.MainActivity;
import com.example.manaadvice.popularmovietmdb.ui.detail.MovieDetailActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    abstract MainActivity mainActivity();

    @ContributesAndroidInjector
    abstract MovieDetailActivity movieDetailActivity();
}
