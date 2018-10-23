package com.example.manaadvice.popularmovietmdb.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.manaadvice.popularmovietmdb.ui.detail.MovieDetailViewModel;
import com.example.manaadvice.popularmovietmdb.ui.grid.MoviesViewModel;
import com.example.manaadvice.popularmovietmdb.utils.MovieViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MoviesViewModel.class)
    abstract ViewModel bindMovieViewModel(MoviesViewModel moviesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel.class)
    abstract ViewModel bindMovieDetailViewModel(MovieDetailViewModel movieDetailViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MovieViewModelFactory factory);

}
