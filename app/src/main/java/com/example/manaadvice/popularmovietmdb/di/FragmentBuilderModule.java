package com.example.manaadvice.popularmovietmdb.di;

import com.example.manaadvice.popularmovietmdb.ui.detail.MovieDetailFragment;
import com.example.manaadvice.popularmovietmdb.ui.grid.MoviesGridFragment;
import com.example.manaadvice.popularmovietmdb.utils.SortingDialogFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    abstract MoviesGridFragment moviesGridFragment();
    @ContributesAndroidInjector
    abstract MovieDetailFragment movieDetailFragment();
    @ContributesAndroidInjector
    abstract SortingDialogFragment sortingDialogFragment();
}
